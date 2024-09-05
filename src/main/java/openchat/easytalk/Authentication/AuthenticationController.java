package openchat.easytalk.Authentication;

import lombok.RequiredArgsConstructor;
import openchat.easytalk.User.Components.DTO.UserDTO;
import openchat.easytalk.User.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        String token = userService.register(userDTO);
        return token == null ?
                new ResponseEntity<>("failed creating token for new registration !!", HttpStatus.BAD_REQUEST) :
                token.startsWith("failed") ?
                        new ResponseEntity<>(token, HttpStatus.BAD_REQUEST)
                        : new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody Map<String, String> request) {
        String res = userService.authenticate(request);
        return !res.startsWith("failed") ?
                new ResponseEntity<>(res, HttpStatus.ACCEPTED)
                : new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/hi")
    public ResponseEntity<String> sayHi() {
        return new ResponseEntity<>("hi from non secure endpoint !", HttpStatus.ACCEPTED);
    }
}
