package openchat.easytalk.Demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import openchat.easytalk.Config.Jwt.JwtService;
import openchat.easytalk.Conversation.ChatRepository;
import openchat.easytalk.GenericDTOs.ListDTO;
import openchat.easytalk.User.User;
import openchat.easytalk.User.UserRepository;
import openchat.easytalk.friendship.FriendRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/demo-controller")
public class DemoController {
    final UserRepository userRepository;
    final FriendRepository friendRepository;
    final JwtService jwtService;
    final ChatRepository chatRepository;
    final MongoTemplate mongoTemplate;

    @GetMapping("/sayHi")
    public ResponseEntity<Object> sayHello() {

        return new ResponseEntity<>(userRepository.findAllWith(), HttpStatus.OK);
    }

    @GetMapping("/secure")
    public ResponseEntity<String> secureEndPoint() {
        return new ResponseEntity<>("Hello from secured endpoint", HttpStatus.OK);

    }

}
