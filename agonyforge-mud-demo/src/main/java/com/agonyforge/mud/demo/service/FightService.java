package com.agonyforge.mud.demo.service;

import com.agonyforge.mud.core.service.dice.DiceService;
import com.agonyforge.mud.core.service.timer.TimerEvent;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.command.HitCommand;
import com.agonyforge.mud.demo.model.constant.AdminFlag;
import com.agonyforge.mud.demo.model.impl.Fight;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.impl.MudRoom;
import com.agonyforge.mud.demo.model.repository.FightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class FightService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FightService.class);

    private final RepositoryBundle repositoryBundle;
    private final CommService commService;
    private final DiceService diceService;
    private final FightRepository fightRepository;

    @Autowired
    public FightService(RepositoryBundle repositoryBundle,
                        CommService commService,
                        DiceService diceService,
                        FightRepository fightRepository) {
        this.repositoryBundle = repositoryBundle;
        this.commService = commService;
        this.diceService = diceService;
        this.fightRepository = fightRepository;
    }

    @EventListener
    @Transactional
    public void onTimerEvent(TimerEvent event) {
        if (!TimeUnit.SECONDS.equals(event.getFrequency())) {
            return;
        }

        List<Fight> fights = fightRepository.findAll();
        List<Fight> ended = new ArrayList<>();

        fights.forEach(fight -> {
            MudCharacter attacker = fight.getAttacker();
            MudCharacter defender = fight.getDefender();

            if (attacker == null || defender == null) {
                LOGGER.warn("Missing attacker or defender");
                ended.add(fight);
            } else if (attacker.getLocation().getRoom() != defender.getLocation().getRoom()) {
                LOGGER.warn("Attacker and defender in different rooms");
                ended.add(fight);
            } else if ((attacker.getPlayer() != null && attacker.getPlayer().getAdminFlags().contains(AdminFlag.PEACEFUL))
                    || (defender.getPlayer() != null && defender.getPlayer().getAdminFlags().contains(AdminFlag.PEACEFUL))) {

                ended.add(fight);

                MudRoom room = attacker.getLocation().getRoom();

                commService.sendTo(attacker, new Output("[white]You grow weary of fighting..."));
                commService.sendTo(defender, new Output("[white]You grow weary of fighting..."));
                commService.sendToRoom(
                    room.getId(),
                    new Output("[white]%s and %s suddenly stop fighting.",
                        attacker.getCharacter().getName(),
                        defender.getCharacter().getName()),
                    attacker, defender);
            } else {
                MudRoom room = attacker.getLocation().getRoom();
                Output chOutput = new Output();
                Output targetOutput = new Output();
                Output roomOutput = new Output();

                LOGGER.debug("Processing fight: ({}) {} vs. {}",
                        fight.getId(),
                        attacker.getCharacter().getName(),
                        defender.getCharacter().getName());

                // the fight was initiated when the attacker attacked the defender
                // if the defender was killed, no Fight should have been created
                // that means it's time for the defender to retaliate
                HitCommand.doHit(repositoryBundle, diceService, fightRepository,
                    targetOutput, chOutput, roomOutput,
                    defender, attacker);

                // if the attacker is still alive, they can attack again
                if (attacker.getCharacter().getHitPoints() > 0) {
                    HitCommand.doHit(repositoryBundle, diceService, fightRepository,
                        chOutput, targetOutput, roomOutput,
                        attacker, defender);
                }

                // send all output
                commService.sendTo(attacker, chOutput);
                commService.sendTo(defender, targetOutput);
                commService.sendToRoom(room.getId(), roomOutput,
                        attacker, defender);

                // is the fight over?
                if (attacker.getCharacter().getHitPoints() <= 0 || defender.getCharacter().getHitPoints() <= 0) {
                    LOGGER.debug("Ended fight: ({})", fight.getId());
                    ended.add(fight);

                    commService.sendTo(attacker, new Output("[white]You are bathed in a white light from above..."));
                    commService.sendTo(defender, new Output("[white]You are bathed in a white light from above..."));
                    commService.sendToRoom(
                        room.getId(),
                        new Output("[white]%s and %s are bathed in a white light from above...",
                            attacker.getCharacter().getName(),
                            defender.getCharacter().getName()),
                        attacker, defender);

                    attacker.getCharacter().setHitPoints(attacker.getCharacter().getMaxHitPoints());
                    defender.getCharacter().setHitPoints(defender.getCharacter().getMaxHitPoints());

                    repositoryBundle.getCharacterRepository().saveAll(List.of(attacker, defender));
                }
            }
        });

        if (!ended.isEmpty()) {
            fightRepository.deleteAll(ended);
        }
    }
}
