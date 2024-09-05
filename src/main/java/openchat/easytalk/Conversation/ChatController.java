package openchat.easytalk.Conversation;

import lombok.RequiredArgsConstructor;
import openchat.easytalk.Config.Jwt.JwtService;
import openchat.easytalk.GenericDTOs.ListDTO;
import openchat.easytalk.Time.DateProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    @PutMapping("/setUnreadMessages")
    public void setUnreadMessages(@RequestBody Map<String, Object> payload) {
        System.out.println(payload);
        chatService.setUnreadMessages((String) payload.get("chatId"),
                (String) payload.get("userId"), (Boolean) payload.get("toIncrement"));
    }

    @PostMapping("/getOrCreate")
    public ResponseEntity<Object> getOrCreate(String friendId) {
        System.out.println(friendId);
        Object tryToDo = chatService.findOrCreate(friendId);
        try {
            List<Message> chat = (List<Message>) tryToDo;
            return new ResponseEntity<>(chat, HttpStatus.OK);
        } catch (Exception e) {
            String info = (String) tryToDo;
            return new ResponseEntity<>(tryToDo, info.startsWith("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

        }

    }


    @MessageMapping("/addUser")
    @SendTo("/topic/public")
    public ResponseEntity<Message> addUser(@Payload Message message
            , SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }


    @DeleteMapping("/deleteMessage")
    public ResponseEntity<Boolean> deleteMessages(String username, @RequestBody ListDTO<String> dates) {
        boolean isDeleted = chatService.deleteMessages(username, dates);

        return new ResponseEntity<>(
                isDeleted, isDeleted ? HttpStatus.OK : HttpStatus.BAD_REQUEST
        );
    }

}
