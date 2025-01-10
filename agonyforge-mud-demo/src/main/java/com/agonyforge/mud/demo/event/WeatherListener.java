package com.agonyforge.mud.demo.event;

import com.agonyforge.mud.core.service.timer.TimerEvent;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.demo.model.constant.RoomFlag;
import com.agonyforge.mud.demo.model.impl.MudProperty;
import com.agonyforge.mud.demo.model.repository.MudPropertyRepository;
import com.agonyforge.mud.demo.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class WeatherListener {
    public static final String PROPERTY_HOUR = "mud.hour";

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherListener.class);
    private static final EnumSet<RoomFlag> ROOM_INDOORS = EnumSet.of(RoomFlag.INDOORS);

    private final MudPropertyRepository mudPropertyRepository;
    private final CommService commService;

    @Autowired
    public WeatherListener(MudPropertyRepository mudPropertyRepository, CommService commService) {
        this.mudPropertyRepository = mudPropertyRepository;
        this.commService = commService;
    }

    @EventListener
    public void onTimerEvent(TimerEvent event) {
        if (!TimeUnit.MINUTES.equals(event.getFrequency())) {
            return;
        }

        final int newHour = advanceTime();

        switch (newHour) {
            case 0 -> commService.sendToAllWithoutFlags(new Output("[dblue]It is midnight."), ROOM_INDOORS);
            case 6 -> commService.sendToAllWithoutFlags(new Output("[dyellow]The sun is rising."), ROOM_INDOORS);
            case 12 -> commService.sendToAllWithoutFlags(new Output("[yellow]It is noon."), ROOM_INDOORS);
            case 18 -> commService.sendToAllWithoutFlags(new Output("[magenta]The sun is setting."), ROOM_INDOORS);
            default -> LOGGER.trace("Advanced MUD hour to {}", newHour);
        }
    }

    private int advanceTime() {
        Optional<MudProperty> mudHourOptional = mudPropertyRepository.findById(PROPERTY_HOUR);
        MudProperty mudHour = mudHourOptional.orElseGet(() -> new MudProperty(PROPERTY_HOUR, "0"));
        int intHour = Integer.parseInt(mudHour.getValue()) + 1;

        if (intHour >= 24) {
            intHour = 0;
        }

        mudHour.setValue(Integer.toString(intHour));

        mudPropertyRepository.save(mudHour);

        return intHour;
    }
}
