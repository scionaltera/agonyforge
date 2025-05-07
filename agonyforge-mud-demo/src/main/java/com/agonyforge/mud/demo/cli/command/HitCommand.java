package com.agonyforge.mud.demo.cli.command;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.service.dice.DiceResult;
import com.agonyforge.mud.core.service.dice.DiceService;
import com.agonyforge.mud.core.web.model.Input;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.model.constant.Effort;
import com.agonyforge.mud.demo.model.constant.WearSlot;
import com.agonyforge.mud.demo.model.impl.Fight;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudItem;
import com.agonyforge.mud.demo.model.repository.FightRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HitCommand extends AbstractCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(HitCommand.class);

    private final DiceService diceService;
    private final FightRepository fightRepository;

    public static void doHit(RepositoryBundle repositoryBundle,
                               DiceService diceService,
                               FightRepository fightRepository,
                               Output chOutput, Output targetOutput, Output roomOutput,
                               MudCharacter ch, MudCharacter target) {
        // Attempt roll
        DiceResult defense = diceService.roll(1, 20, target.getCharacter().getDefense());
        final int attemptTarget = defense.getModifiedRoll(0);

        DiceResult attempt = diceService.roll(1, 20);
        LOGGER.trace("Attempt result: {}", attempt);

        if (attempt.getModifiedRoll(0) >= attemptTarget) {
            // For now, anything held is a weapon
            Optional<MudItem> weaponOptional = repositoryBundle.getItemRepository().findByLocationHeld(ch)
                .stream()
                .filter(item -> item.getLocation().getWorn().contains(WearSlot.HELD_MAIN))
                .findFirst();

            // Damage roll
            // d4 if BASIC effort, d6 if WEAPONS & TOOLS
            DiceResult damage = diceService.roll(1, weaponOptional.isPresent() ? 6 : 4);
            int damageAmount = damage.getModifiedRoll(0);

            LOGGER.trace("Damage result: {}", damage);

            // natural 20 adds ULTIMATE
            if (attempt.getRoll(0) == 20) {
                DiceResult ultimate = diceService.roll(1, 12, ch.getCharacter().getEffort(Effort.ULTIMATE));
                damageAmount += ultimate.getModifiedRoll(0);

                LOGGER.trace("Ultimate result: {}", ultimate);
            }

            // TODO check if any equipped items provide any relevant bonuses

            int targetHitPoints = target.getCharacter().getHitPoints();
            int resultHitPoints = Math.max(targetHitPoints - damageAmount, 0); // don't go below 0

            // TODO in the future we may go below 0 because of the "blown to bits" at -20 rule

            chOutput.append("[default]You hit %s!", target.getCharacter().getName());
            targetOutput.append("[default]%s hits you!", ch.getCharacter().getName());
            roomOutput.append("[default]%s hits %s!", ch.getCharacter().getName(), target.getCharacter().getName());

            chOutput.append("[green]You did %d damage!", damageAmount);
            targetOutput.append("[red]%s did %d damage!",  ch.getCharacter().getName(), damageAmount);
            target.getCharacter().setHitPoints(resultHitPoints);
            repositoryBundle.getCharacterRepository().save(target);
        } else {
            chOutput.append("[default]You miss.");
            targetOutput.append(new Output("[default]%s tries to hit you, but misses.", ch.getCharacter().getName()));
            roomOutput.append("[default]%s tries to hit %s, but misses.", ch.getCharacter().getName(), target.getCharacter().getName());
        }

        Optional<Fight> fightOptional = fightRepository.findByAttackerAndDefender(ch, target)
            .or(() -> fightRepository.findByAttackerAndDefender(target, ch));

        if (fightOptional.isEmpty()) {
            Fight fight = new Fight();

            fight.setAttacker(ch);
            fight.setDefender(target);

            fightRepository.save(fight);

            LOGGER.debug("Created fight: ({}) {} vs. {}",
                fight.getId(),
                fight.getAttacker().getCharacter().getName(),
                fight.getDefender().getCharacter().getName());
        }
    }

    @Autowired
    public HitCommand(RepositoryBundle repositoryBundle,
                      CommService commService,
                      DiceService diceService,
                      FightRepository fightRepository,
                      ApplicationContext applicationContext) {
        super(repositoryBundle, commService, applicationContext);
        this.diceService = diceService;
        this.fightRepository = fightRepository;
    }

    @Override
    public Question execute(Question question, WebSocketContext webSocketContext, List<String> tokens, Input input, Output output) {
        if (tokens.size() <= 1) {
            output.append("[default]Who do you want to hit?");
            return question;
        }

        MudCharacter ch = getCurrentCharacter(webSocketContext, output);
        Optional<MudCharacter> targetOptional = findRoomCharacter(ch, tokens.get(1));

        if (targetOptional.isEmpty()) {
            output.append("[default]You don't see anyone like that here.");
            return question;
        }

        MudCharacter target = targetOptional.get();
        Output targetOutput = new Output();
        Output roomOutput = new Output();

        doHit(getRepositoryBundle(), diceService, fightRepository, output, targetOutput, roomOutput, ch, target);

        getCommService().sendTo(target, targetOutput);
        getCommService().sendToRoom(ch.getLocation().getRoom().getId(), roomOutput, ch, target);

        return question;
    }
}
