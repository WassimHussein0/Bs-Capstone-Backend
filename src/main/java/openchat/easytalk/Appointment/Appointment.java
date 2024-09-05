package openchat.easytalk.Appointment;

import lombok.*;
import openchat.easytalk.Conversation.Chat;
import openchat.easytalk.User.Doctor;
import openchat.easytalk.User.Patient;
import openchat.easytalk.User.User;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;
    @NonNull

    @Indexed
    private String doctorId;

    @NonNull
    @Indexed
    private String patientId;

    @NonNull

    private String chatId;

    private Report report;
    @NonNull
    private double duration;
    @NonNull
    private double cost;
    private long appointmentDate; // timestamp contains year, month, day and starts time
    @Indexed(unique = true)
    private long key;

    Appointment(Doctor doctor, Patient patient, String chatId, long date) {
        this.doctorId = doctor.getId();
        this.patientId = patient.getId();
        this.chatId = chatId;
        duration = doctor.getAppointmentDuration();
        cost = doctor.getCostPerAppointment();
        this.appointmentDate = date;
        this.report = new Report();
        key = System.currentTimeMillis();
        id = new ObjectId().toString();

    }


}
