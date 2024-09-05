package openchat.easytalk.Appointment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/makeAppointment")
    public ResponseEntity<String> makeAppointment(@RequestBody Map<String, Object> payload) {
        String res = appointmentService.takeAppointment(payload);
        return new ResponseEntity<>(res, res.startsWith("failed") ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED);
    }

    @DeleteMapping("/cancelAppointment")
    public ResponseEntity<String> cancelAppointment(String appointmentId) {
        String res = appointmentService.cancelAppointment(appointmentId);
        return new ResponseEntity<>(res, res.startsWith("failed") ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED);
    }

    @GetMapping("/getDoctorWithAppointments")
    public ResponseEntity<Object> getDoctorWithAppointments(Long doctorJoinDate) {
        Object o = appointmentService.getDoctorWithAppDates(doctorJoinDate);
        return new ResponseEntity<>(o, o.toString().startsWith("failed") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @GetMapping("/getMedicalHistory")
    public ResponseEntity<Object> getMedicalHistory(String patientId) {
        System.out.println(patientId);
        Object o = appointmentService.getMedicalHistory(patientId);
        return new ResponseEntity<>(o, o.toString().startsWith("failed") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }


    @GetMapping("/getReport")
    public ResponseEntity<Object> findReport(Long appBookingDate) {
        Object o = appointmentService.findReport(appBookingDate);
        return new ResponseEntity<>(o, o.toString().startsWith("failed") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @GetMapping("/getChats")
    public ResponseEntity<Object> getChats() {
        Object o = appointmentService.findAllChats();
        return new ResponseEntity<>(o, o.toString().startsWith("error") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @PostMapping("/setReport")
    public ResponseEntity<Object> setReport(Long appBookingDate, @RequestBody Report report) {
        if (appBookingDate == null || report == null) return
                new ResponseEntity<>("error, wrong parameters", HttpStatus.BAD_REQUEST);
        Object o = appointmentService.setReport(appBookingDate, report);
        return new ResponseEntity<>(o, o.toString().startsWith("error") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @GetMapping("/getAppointments")
    public ResponseEntity<Object> getAppointments(String state) {
        boolean isCurrent = state == null ? true : state.equalsIgnoreCase("pending");
        Object apps = appointmentService.findAppointments(isCurrent);
        return new ResponseEntity<>(apps, apps.toString().startsWith("error") ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

}
