package openchat.easytalk.User;

import lombok.RequiredArgsConstructor;
import openchat.easytalk.Config.Jwt.JwtService;
import openchat.easytalk.User.Components.DTO.UserDTO;
import openchat.easytalk.User.Components.Enums.Role;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {


    private static List<Object> doctors;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MongoTemplate mongoTemplate;

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


    public Optional<List<Object>> findAllByUsernamesWith(List<String> usernames) {
        return Optional.ofNullable(userRepository.findAllByUsernameWith(usernames));
    }

    public Optional<List<Object>> findAllByRole(String role) {
        return userRepository.findAllBy("role", role);
    }

    public List<Object> findAllApprovedDoctors() {
        if (doctors != null) return doctors;
        Optional<List<Object>> optionalDoctors =
                userRepository.findAllByTwoFieldNames("role", Role.DOCTOR.name(), "isApproved", true);
        if (!optionalDoctors.isPresent()) return new ArrayList<>();
        return optionalDoctors.get();
    }

    public String rateDoctor(String doctorId, Integer rating) {
        String patientId = JwtService.extractGivenClaim("id").toString();
        Optional<User> user = userRepository.findById(doctorId);
        if (user.isPresent()) {
            Doctor doctor = (Doctor) user.get();
            if (doctor.getRatings() == null) {
                doctor.setRatings(Map.of(patientId, rating));
            } else {
                doctor.getRatings().put(patientId, rating);

            }
            userRepository.save(doctor);
            return "success, doctor has been rated";
        }
        return "failed, cannot rate doctor";
    }

    public String approveDoctor(Map<String, String> payload) {
        try {
            Doctor getDoctor = (Doctor) userRepository.findById(payload.get("id")).get();
            getDoctor.setApproved(payload.get("approve").equals("true"));
            userRepository.save(getDoctor);
            return "success, doctor account is " +
                    (payload.get("approve").equals("true") ? "approved" : "disapproved");
        } catch (Exception e) {
            System.out.println(e);
            return "failed," + e.getMessage();
        }
    }

    public void setProfilePicture(String picture, String id) {
        Optional<User> u = userRepository.findById(id);
        if (u.isPresent()) {
            User user = u.get();
            user.setPicture(picture);
            userRepository.save(user);
        }
    }

    public void setLastSeen(String userId, Long lastSeen) {
        Optional<User> u = userRepository.findById(userId);
        if (u.isPresent()) {
            User user = u.get();
            user.setLastSeen(lastSeen);
            userRepository.save(user);
        }
    }

    public String register(UserDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return "failed, username already exist !!";
        } else if (userRepository.existsByEmail(request.getEmail())) {
            return "failed, email already exist !!";
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        String role = request.getRole().toLowerCase();
        User user = new User();
        if (role.equals("patient"))
            user = new Patient(request);

        else if (role.equals("doctor"))
            user = new Doctor(request);

        userRepository.insert(user);

        return jwtService.generateToken(user);
    }

    public String authenticate(Map<String, String> request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.get("username"), request.get("password"))
            );
            var user = userRepository.findByUniqueIdentifier(request.get("username"))
                    .orElseThrow();
            return jwtService.generateToken(user);
        } catch (BadCredentialsException badCredentials) {
            return "failed, Incorrect password or user not found";
        } catch (AuthenticationException e) {
            return "failed authentication!" + e.getMessage();
        }

    }

    public boolean delete() {
        try {
            userRepository.delete((String) JwtService.extractGivenClaim("id"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Object findById() {
        try {
            return userRepository.findUserById((String) JwtService.extractGivenClaim("id"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    public User findByUniqueIdentifier(String identity) {
        Optional<User> user = userRepository.findByUniqueIdentifier(identity);
        return user.isPresent() ? user.get() : null;

    }


    public Object updateProfile(Map<String, Object> fieldsToUpdate) {
        String userId = JwtService.extractGivenClaim("id").toString();
        String role = JwtService.extractGivenClaim("role").toString().toLowerCase();

        Optional<User> tryFindUser = userRepository.findById(userId);

        if (tryFindUser.isPresent()) {
            User user = tryFindUser.get();
            for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                user.setField(key, value);
            }
            userRepository.save(user);
            return "success, account has been updated";
        }
        return "failed, cannot update the account";
    }


}
