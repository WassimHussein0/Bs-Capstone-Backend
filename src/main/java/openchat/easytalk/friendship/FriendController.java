package openchat.easytalk.friendship;

import lombok.RequiredArgsConstructor;
import openchat.easytalk.Config.Jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/friends")
public class FriendController {

    final FriendService friendService;

    @PostMapping("/addFriend")
    public ResponseEntity<String> addFriend(String friendUsername) {
        String username = JwtService.extractUsername(JwtService.getToken());

        String isAdded = friendService.addFriend(friendUsername, username);
        return new ResponseEntity<>(isAdded, isAdded.startsWith("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
