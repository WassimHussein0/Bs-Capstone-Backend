package openchat.easytalk.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import openchat.easytalk.User.Components.DTO.UserDTO;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@Document(collection = "users")
public class Doctor extends User {

    Double costPerAppointment;
    Integer appointmentDuration;
    Map<String, Integer> ratings;
    String profession;
    Object schedule;
    boolean isApproved;

    Doctor(UserDTO user) {
        super(user);
        costPerAppointment = user.getCost();
        profession = user.getProfession();
        appointmentDuration = user.getDuration();
        schedule = user.getSchedule();
        ratings = null;

    }

    @Override
    public void setField(String fieldName, Object value) {
        switch (fieldName) {
            case "costPerAppointment":
                setCostPerAppointment((Double) value);
                break;
            case "appointmentDuration":
                setAppointmentDuration((Integer) value);
                break;
            case "profession":
                setProfession(value.toString());
                break;
            case "schedule":
                setSchedule(value);
                break;
            default:
                super.setField(fieldName, value);
        }


    }
}
