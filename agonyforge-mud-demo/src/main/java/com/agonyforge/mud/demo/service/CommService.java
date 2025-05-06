package com.agonyforge.mud.demo.service;

import com.agonyforge.mud.core.cli.Question;
import com.agonyforge.mud.core.cli.Response;
import com.agonyforge.mud.core.web.controller.WebSocketContextAware; // ← inject this!
import com.agonyforge.mud.core.service.EchoService;
import com.agonyforge.mud.core.service.SessionAttributeService;
import com.agonyforge.mud.core.service.StompPrincipal;
import com.agonyforge.mud.core.web.model.Input;
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

import static com.agonyforge.mud.core.config.SessionConfiguration.MUD_QUESTION;

@Component
public class CommService extends EchoService {
        private final WebSocketContextAware webSocketContextAware;
        private final MudCharacterRepository characterRepository;
        private final InputProcessingService inputProcessingService;

        @Autowired
        public CommService(ApplicationContext applicationContext,
                        SimpMessagingTemplate simpMessagingTemplate,
                        SimpUserRegistry simpUserRegistry,
                        SessionAttributeService sessionAttributeService,
                        WebSocketContextAware webSocketContextAware, // ← here
                        MudCharacterRepository characterRepository,
                        InputProcessingService inputProcessingService) {
                super(applicationContext, simpMessagingTemplate, simpUserRegistry, sessionAttributeService);
                this.webSocketContextAware = webSocketContextAware;
                this.characterRepository = characterRepository;
                this.inputProcessingService = inputProcessingService;
        }

        /**
         * Send a message to all characters that are playing except the sender and
         * specifically excluded
         * characters.
         *
         * @param wsContext The WebSocketContext of the sender.
         * @param message   The message to send.
         * @param except    Don't send to these characters.
         */
        public void sendToAll(WebSocketContext wsContext, Output message, MudCharacter... except) {
                List<MudCharacter> skip = List.of(except);

                characterRepository.findAll()
                                .stream()
                                .filter(ch -> ch.getPlayer() != null)
                                .filter(ch -> !wsContext.getSessionId().equals(ch.getPlayer().getWebSocketSession()))
                                .filter(ch -> !skip.contains(ch))
                                .forEach(ch -> sendTo(ch, message));
        }

        /**
         * Send a message to all characters except specifically excluded characters.
         *
         * @param message The message to send.
         * @param except  Don't send to these characters.
         */
        public void sendToAll(Output message, MudCharacter... except) {
                List<MudCharacter> skip = List.of(except);

                characterRepository.findAll()
                                .stream()
                                .filter(ch -> !skip.contains(ch))
                                .forEach(ch -> sendTo(ch, message));
        }

        /**
         * Send a message to all characters in rooms without the specified flags.
         * 
         * @param message   The message to send.
         * @param roomFlags Don't send if the character's room has all of these flags.
         * @param except    Don't send to these characters.
         */
        public void sendToAllWithoutFlags(Output message, EnumSet<RoomFlag> roomFlags, MudCharacter... except) {
                List<MudCharacter> skip = List.of(except);

                characterRepository.findAll()
                                .stream()
                                .filter(ch -> !skip.contains(ch))
                                .filter(ch -> ch.getLocation() != null && ch.getLocation().getRoom() != null
                                                && !ch.getLocation().getRoom().getFlags().containsAll(roomFlags))
                                .forEach(ch -> sendTo(ch, message));
        }

        /**
         * Send a message to all characters in a Zone.
         *
         * @param wsContext The WebSocketContext of the sender.
         * @param message   The message to send.
         * @param except    Don't send to these characters.
         */
        public void sendToZone(WebSocketContext wsContext, Long zoneId, Output message, MudCharacter... except) {
                String zoneIdString = zoneId.toString();
                List<MudCharacter> skip = List.of(except);

                characterRepository.findByLocationRoomIdBetween(zoneId * 100, zoneId * 100 + 100)
                                .stream()
                                .filter(ch -> ch.getPlayer() != null)
                                .filter(ch -> !wsContext.getSessionId().equals(ch.getPlayer().getWebSocketSession()))
                                .filter(ch -> !skip.contains(ch))
                                .filter(ch -> zoneIdString
                                                .equals(ch.getLocation().getRoom().getId().toString().substring(0,
                                                                zoneIdString.length())))
                                .forEach(ch -> sendTo(ch, message));
        }

        /**
         * Send a message to all characters in a Room.
         *
         * @param roomId  The ID of the room to send to.
         * @param message The message to send.
         * @param except  Don't send to these characters.
         */
        public void sendToRoom(Long roomId, Output message, MudCharacter... except) {
                List<MudCharacter> skip = List.of(except);
                characterRepository.findByLocationRoomId(roomId)
                                .stream()
                                .filter(ch -> ch.getPlayer() != null)
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
         * @param ch      The character to send to.
         * @param message The message to send.
         */
        public void sendTo(MudCharacter ch, Output message) {
                if (ch.getPlayer() == null) {
                        return;
                }

                Map<String, Object> attributes = sessionAttributeService
                                .getSessionAttributes(ch.getPlayer().getWebSocketSession());

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

        public void executeCommandAs(WebSocketContext originalContext, MudCharacter target, String command) {
                if (target.getPlayer() == null) {
                        return;
                }

                // 1) Retrieve the target's session attributes
                Map<String, Object> attributes = sessionAttributeService
                                .getSessionAttributes(target.getPlayer().getWebSocketSession());

                // 2) Rebuild a WebSocketContext for the target
                WebSocketContext targetWsContext = WebSocketContext.build(
                                new StompPrincipal(target.getPlayer().getUsername()),
                                target.getPlayer().getWebSocketSession(),
                                attributes);

                // 3) Set thread-local context so Questions see the right session
                webSocketContextAware.setWebSocketContext(targetWsContext);

                // 4) Process the forced input via your shared pipeline
                Input input = new Input(command);
                Output output = inputProcessingService.processInput(targetWsContext, input);

                // 5) Send the resulting Output back to the target on /queue/output
                MessageHeaders headers = buildMessageHeaders(target.getPlayer().getWebSocketSession());
                simpMessagingTemplate.convertAndSendToUser(
                                target.getPlayer().getUsername(),
                                "/queue/output",
                                output,
                                headers);
        }
}
