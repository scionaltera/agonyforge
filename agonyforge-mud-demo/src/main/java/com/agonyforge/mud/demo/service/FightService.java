package com.agonyforge.mud.demo.service;

import com.agonyforge.mud.core.service.dice.DiceService;
import com.agonyforge.mud.core.service.timer.TimerEvent;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.cli.RepositoryBundle;
import com.agonyforge.mud.demo.cli.command.HitCommand;
import com.agonyforge.mud.demo.model.impl.Fight;
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
            Output chOutput = new Output();
            Output targetOutput = new Output();
            Output roomOutput = new Output();

            LOGGER.debug("Processing fight: ({}) {} vs. {}",
                fight.getId(),
                fight.getAttacker().getCharacter().getName(),
                fight.getDefender().getCharacter().getName());

            // attacker attacks
            HitCommand.doHit(repositoryBundle, diceService, fightRepository,
                chOutput, targetOutput, roomOutput,
                fight.getAttacker(), fight.getDefender());

            // defender attacks
            HitCommand.doHit(repositoryBundle, diceService, fightRepository,
                targetOutput, chOutput, roomOutput,
                fight.getDefender(), fight.getAttacker());

            // send all output
            commService.sendTo(fight.getAttacker(), chOutput);
            commService.sendTo(fight.getDefender(), targetOutput);
            commService.sendToRoom(fight.getAttacker().getLocation().getId(), roomOutput,
                fight.getAttacker(), fight.getDefender());

            // is the fight over?
            if (fight.getAttacker().getCharacter().getHitPoints() <= 0 || fight.getDefender().getCharacter().getHitPoints() <= 0) {
                LOGGER.debug("Ended fight: ({})", fight.getId());
                ended.add(fight);
            }
        });

        if (!ended.isEmpty()) {
            fightRepository.deleteAll(ended);
        }
    }
}
