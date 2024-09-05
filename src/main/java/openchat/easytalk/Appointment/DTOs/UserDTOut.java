package openchat.easytalk.Appointment.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import openchat.easytalk.User.Components.Enums.Gender;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTOut {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String id;
    private String birthday;
    private Gender gender;
    private String picture;
    private String role;
    private String bio;
    private String profession;
    private Double cost;
    private Integer duration;
    private Integer rating;
    private Long lastSeen;
}
