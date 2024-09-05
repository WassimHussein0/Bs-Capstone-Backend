package openchat.easytalk.User;


import lombok.RequiredArgsConstructor;
import openchat.easytalk.User.Components.Enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final UserService userService;

    @GetMapping("/getDoctors")
    public ResponseEntity<List<Object>> getDoctors() {
        return new ResponseEntity<>(userService.findAllByRole(Role.DOCTOR.name()).get(), HttpStatus.OK);
    }

    @PostMapping("/approveDoctor")
    public ResponseEntity<String> approveDoctor(@RequestBody Map<String, String> payload) {
        String res = userService.approveDoctor(payload);
        return new ResponseEntity<>(res, res.startsWith("success") ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
