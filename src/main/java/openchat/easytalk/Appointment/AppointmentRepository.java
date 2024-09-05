package openchat.easytalk.Appointment;

import openchat.easytalk.User.Doctor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    @Query(value = "{doctorId:   ?0}")
    Optional<List<Appointment>> findAllByDoctorId(String doctorId);

    @Query(value = "{  doctorId: ?0}", sort = "{appointmentDate:  1} ", fields = "{doctorId: 0}")
        // the key represents the booking timestamp
    Optional<List<Appointment>> findAppointmentsByDoctor(String id);

    @Query(value = "{patientId : ?0}", sort = "{appointmentDate:  1}", fields = "{patientId:  0}")
        // the key represents the booking timestamp
    Optional<List<Appointment>> findAppointmentsByPatient(String id);

    @Query(value = "{key:  ?0}")
    Optional<Appointment> findReport(Long bookingDate);

    @Query(value = "{_id:  ?0}", delete = true)
    Integer removeById(String bookingDate);

    @Query(value = "{chatId:  ?0}", count = true)
    Integer getAppointmentsLengthByChatId(String chatId);

}

