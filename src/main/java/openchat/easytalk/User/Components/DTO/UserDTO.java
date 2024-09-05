package openchat.easytalk.User.Components.DTO;

import jakarta.annotation.Nullable;
import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class UserDTO {
    @NonNull
    private String role;

    private String firstName;

    private String lastName;
    @NonNull
    private String username;

    private String gender;

    private String email;
    @NonNull
    private String password;
    private long joinDate;

    private String picture;
    private String profession;
    private Map<String, Integer> ratings;
    private String birthday;
    private String bio;
    private String country;
    private String city;
    private Double cost;
    private Integer duration;
    private Object schedule;


}
