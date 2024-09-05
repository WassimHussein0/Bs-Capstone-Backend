package openchat.easytalk.User;

import lombok.*;
import openchat.easytalk.User.Components.DTO.UserDTO;
import openchat.easytalk.User.Components.Enums.Gender;
import openchat.easytalk.User.Components.Enums.Role;
import openchat.easytalk.User.Components.ModelsComponents.Address;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User implements UserDetails {

    @NonNull
    String password;


    @Id
    @Indexed
    private String id;
    private String firstName;
    private String lastName;
    @Indexed(unique = true)
    private String username;
    private Gender gender;
    private String birthday;
    private Long joinDate;
    @Indexed(unique = true)
    private String email;
    private String bio;
    private Address address;

    private Role role;
    private String picture;
    @DocumentReference
    private List<User> friends;

    private Long lastSeen;

    // RELATIONS

    User(UserDTO user) {
        picture = user.getPicture();
        role = Role.valueOf(user.getRole().toUpperCase());
        password = user.getPassword();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        username = user.getUsername();
        email = user.getEmail();
        birthday = user.getBirthday();
        gender = Gender.valueOf(user.getGender().toUpperCase());
        joinDate = user.getJoinDate();
        bio = user.getBio();
        lastSeen = 0L;

    }

    User(UserDTO user, boolean forAdmin) {
        role = Role.valueOf(user.getRole().toUpperCase());
        password = user.getPassword();
        username = user.getUsername();
        email = user.getEmail();

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public Long getJoinDate() {
        return joinDate;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public Address getAddress() {
        return address;
    }

    public Role getRole() {
        return role;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setField(String fieldName, Object value) {
        switch (fieldName) {
            case "firstname":
                setFirstName(value.toString());
                break;
            case "lastName":
                setLastName(value.toString());
                break;
            case "username":
                setUsername(value.toString());
                break;

            case "picture":
                setPicture(value.toString());
                break;
            case "email":
                setEmail(value.toString());
                break;
            case "gender":
                setGender(Gender.valueOf(value.toString().toUpperCase()));
                break;
            case "bio":
                setBio(value.toString());
                break;
            case "birthday":
                setBirthday(value.toString());
                break;
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
