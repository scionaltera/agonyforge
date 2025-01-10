package com.agonyforge.mud.demo.service;

import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.service.StompPrincipal;
import com.agonyforge.mud.core.web.model.Output;
import com.agonyforge.mud.core.web.model.WebSocketContext;
import com.agonyforge.mud.demo.model.constant.RoomFlag;
import com.agonyforge.mud.demo.model.impl.MudCharacter;
import com.agonyforge.mud.demo.model.repository.MudCharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@Component
public class CommService extends EchoService {
    private final MudCharacterRepository characterRepository;

    @Autowired
    public CommService(ApplicationContext applicationContext,
                       SimpMessagingTemplate simpMessagingTemplate,
                       SimpUserRegistry simpUserRegistry,
                       SessionAttributeService sessionAttributeService,
                       MudCharacterRepository characterRepository) {
        super(applicationContext, simpMessagingTemplate, simpUserRegistry, sessionAttributeService);

        this.characterRepository = characterRepository;
    }

    /**
     * Send a message to all characters that are playing except the sender and specifically excluded
     * characters.
     *
     * @param wsContext The WebSocketContext of the sender.
     * @param message The message to send.
     * @param except Don't send to these characters.
     */
    public void sendToAll(WebSocketContext wsContext, Output message, MudCharacter ... except) {
        List<MudCharacter> skip = List.of(except);

        characterRepository.findAll()
            .stream()
            .filter(ch -> !wsContext.getSessionId().equals(ch.getPlayer().getWebSocketSession()))
            .filter(ch -> !skip.contains(ch))
            .forEach(ch -> sendTo(ch, message));
    }

    /**
     * Send a message to all characters except specifically excluded characters.
     *
     * @param message The message to send.
     * @param except Don't send to these characters.
     */
    public void sendToAll(Output message, MudCharacter ... except) {
        List<MudCharacter> skip = List.of(except);

        characterRepository.findAll()
            .stream()
            .filter(ch -> !skip.contains(ch))
            .forEach(ch -> sendTo(ch, message));
    }

    /**
     * Send a message to all characters in rooms without the specified flags.
     * @param message The message to send.
     * @param roomFlags Don't send if the character's room has all of these flags.
     * @param except Don't send to these characters.
     */
    public void sendToAllWithoutFlags(Output message, EnumSet<RoomFlag> roomFlags, MudCharacter ... except) {
        List<MudCharacter> skip = List.of(except);

        characterRepository.findAll()
            .stream()
            .filter(ch -> !skip.contains(ch))
            .filter(ch -> ch.getLocation() != null && ch.getLocation().getRoom() != null && !ch.getLocation().getRoom().getFlags().containsAll(roomFlags))
            .forEach(ch -> sendTo(ch, message));
    }

    /**
     * Send a message to all characters in a Zone.
     *
     * @param wsContext The WebSocketContext of the sender.
     * @param message The message to send.
     * @param except Don't send to these characters.
     */
    public void sendToZone(WebSocketContext wsContext, Long zoneId, Output message, MudCharacter ... except) {
        String zoneIdString = zoneId.toString();
        List<MudCharacter> skip = List.of(except);

        characterRepository.findByLocationRoomIdBetween(zoneId * 100, zoneId * 100 + 100)
            .stream()
            .filter(ch -> !wsContext.getSessionId().equals(ch.getPlayer().getWebSocketSession()))
            .filter(ch -> !skip.contains(ch))
            .filter(ch -> zoneIdString.equals(ch.getLocation().getRoom().getId().toString().substring(0, zoneIdString.length())))
            .forEach(ch -> sendTo(ch, message));
    }

    /**
     * Send a message to all characters in a Room.
     *
     * @param wsContext The WebSocketContext of the sender.
     * @param roomId The ID of the room to send to.
     * @param message The message to send.
     * @param except Don't send to these characters.
     */
    public void sendToRoom(WebSocketContext wsContext, Long roomId, Output message, MudCharacter ... except) {
        List<MudCharacter> skip = List.of(except);
        characterRepository.findByLocationRoomId(roomId)
            .stream()
            .filter(ch -> !wsContext.getSessionId().equals(ch.getPlayer().getWebSocketSession()))
            .filter(ch -> !skip.contains(ch))
            .forEach(ch -> sendTo(ch, message));
    }

    /**
     * Send a message to the provided list of characters.
     *
     * @param targets The characters to send to.
     * @param message The message to send.
     */
    public void sendToTargets(List<MudCharacter> targets, Output message) {
        targets
            .forEach(ch -> sendTo(ch, message));
    }

    /**
     * Send a message to the provided list of characters.
     *
     * @param ch The character to send to.
     * @param message The message to send.
     */
    public void sendTo(MudCharacter ch, Output message) {
        Map<String, Object> attributes = sessionAttributeService.getSessionAttributes(ch.getPlayer().getWebSocketSession());

        if (!"commandQuestion".equals(attributes.get("MUD.QUESTION"))) {
            return;
        }

        WebSocketContext targetWsContext = WebSocketContext.build(
            new StompPrincipal(ch.getPlayer().getUsername()),
            ch.getPlayer().getWebSocketSession(),
            attributes);

        Output messageWithPrompt = appendPrompt(targetWsContext, message);
        MessageHeaders messageHeaders = buildMessageHeaders(ch.getPlayer().getWebSocketSession());

        simpMessagingTemplate.convertAndSendToUser(
            targetWsContext.getPrincipal().getName(),
            "/queue/output",
            messageWithPrompt,
            messageHeaders);
    }
}
