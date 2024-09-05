package openchat.easytalk.User;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import openchat.easytalk.Config.Jwt.JwtService;
import openchat.easytalk.GenericDTOs.ListDTO;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;


    @GetMapping("/hi")
    public ResponseEntity<Object> toStringJWT() {
        return new ResponseEntity<>
                (JwtService.extractGivenClaim("id"), HttpStatus.OK);
    }

    @GetMapping("/find")
    public ResponseEntity<Object> findUser(String username) {
        User user = userService.findByUniqueIdentifier(username);
        return user == null ? new ResponseEntity<>("failed, user not found", HttpStatus.BAD_REQUEST)
                : new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PutMapping("setImage")
    public ResponseEntity<?> setImage(@RequestBody Map<String, String> payload) {
        userService.setProfilePicture(payload.get("picture"), payload.get("userId"));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PutMapping("updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> payload) {
        String res = userService.updateProfile(payload).toString();

        return new ResponseEntity<>(res, res.startsWith("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("rateDoctor")
    public ResponseEntity<String> rateDoctor(@RequestBody Map<String, Object> payload) {
        String res = userService.rateDoctor(payload.get("doctorId").toString()
                , (Integer) payload.get("rating"));
        return new ResponseEntity<>(res, res.startsWith("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/findAllDoctors")
    public ResponseEntity<Object> findAllDoctors() {
        try {
            return new ResponseEntity<>(userService.findAllApprovedDoctors(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);

        }
    }

    @GetMapping("/findById")
    public ResponseEntity<Object> findUserById() {
        Object user = userService.findById();
        return user == null ? new ResponseEntity<>("failed, user not found", HttpStatus.BAD_REQUEST)
                : new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/findAll")
    public ResponseEntity<Object> findAllByUsernamesWith(@RequestBody ListDTO<String> usernames) {
        try {
            if (usernames.getValues().isEmpty())
                return new ResponseEntity<>("failed, no usernames provided", HttpStatus.BAD_REQUEST);
            Optional<List<Object>> users = userService.findAllByUsernamesWith(usernames.getValues());
            return !users.isPresent() ?
                    new ResponseEntity<>("failed, username/s are not found", HttpStatus.BAD_REQUEST)
                    : new ResponseEntity<>(users.get(), HttpStatus.OK);
        } catch (NullPointerException nullPointer) {
            return new ResponseEntity<>("failed, no provided usernames with 'values'", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete() {
        boolean isDeleted = userService.delete();
        return isDeleted ? new ResponseEntity<>("success, account is deleted", HttpStatus.OK)
                : new ResponseEntity<>("failed, account cannot be deleted", HttpStatus.BAD_REQUEST);
    }


}
