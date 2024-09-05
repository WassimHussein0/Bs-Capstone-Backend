package openchat.easytalk.Conversation;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/private")
    public Object addMessage(@Payload Map<String, Object> messageBody) {
        Message message = new Message((String) messageBody.get("body"),
                (Long) messageBody.get("sentAt"), (String) messageBody.get("sender"));
        System.out.println(message);
        return message;
//        System.out.println(sendTo);
//        System.out.println(message);
//        boolean isAdded = chatService.add(sendTo, message);
//        return isAdded ? new ResponseEntity<>(message, HttpStatus.CREATED)
//                : new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);


    }

    @MessageMapping("/private-{chatId}")
    public void sendToOtherUser(@Payload Map<String, Object> msgObj, @DestinationVariable String chatId, @Header("simpSessionId") String sessionId) {
        System.out.println(chatId);
        simpMessagingTemplate.convertAndSend("/queue/reply-" + chatId, msgObj);
    }
}
