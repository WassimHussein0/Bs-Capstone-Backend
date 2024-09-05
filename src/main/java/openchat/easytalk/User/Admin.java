package openchat.easytalk.User;

import lombok.NoArgsConstructor;
import openchat.easytalk.User.Components.DTO.UserDTO;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Document(collection = "users")

public class Admin extends User {

    Admin(UserDTO user) {
        super(user, true);
    }
}
