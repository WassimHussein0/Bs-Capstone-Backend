package openchat.easytalk.Appointment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import openchat.easytalk.Appointment.DTOs.UserDTOut;
import openchat.easytalk.Config.Jwt.JwtService;
import openchat.easytalk.Config.WebSocketEventListener;
import openchat.easytalk.Conversation.Chat;
import openchat.easytalk.Conversation.ChatRepository;
import openchat.easytalk.Conversation.ChatService;
import openchat.easytalk.User.*;
import openchat.easytalk.User.Components.Enums.Role;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final MongoTemplate mongoTemplate;
    private final ChatRepository chatRepository;
    private final SimpMessageSendingOperations messageSendingOperations;
    private final WebSocketEventListener webSocketEventListener;


    public String takeAppointment(Map<String, Object> payload) {
        try {
            Long l1 = (Long) payload.get("doctor"), l2 = (Long) JwtService.extractGivenClaim("joinDate");
            if (l1 == l2) return "failed, doctor and patient are the same !!";
            List<User> users = userRepository.findAllByJoinDate(List.of(l1, l2));
            if (users.size() != 2) return "failed, doctor not found";
            Chat chat = chatService.findOrCreateChat(users.get(0), users.get(1));

            if (chat == null) return "failed, error creating chat for the appointment !!";
            Doctor doctor = (Doctor) (users.get(0).getRole() == Role.DOCTOR ? users.get(0) : users.get(1));
            Patient patient = (Patient) (users.get(0).getRole() == Role.PATIENT ? users.get(0) : users.get(1));
            Appointment appToAdd = new Appointment(
                    doctor,
                    patient,
                    chat.getId(), (Long) payload.get("appointmentDate"));

            Appointment appointment = appointmentRepository.save(appToAdd);
            Map<String, Object> newAppTOReturn = new HashMap<>();
            boolean isDoctor = Role.DOCTOR.name().equals(JwtService.extractGivenClaim("role").toString());
            newAppTOReturn.put("chat", chat.getMessages());
            newAppTOReturn.put("unreadMessages", 0);
            newAppTOReturn.put("doctor", filterUser(doctor));
            newAppTOReturn.put("patient", filterUser(patient));
            newAppTOReturn.put("appointments", List.of(filterApp(appointment)));
            newAppTOReturn.put("id", chat.getId());

            messageSendingOperations.convertAndSend("/topic/appointmentReservation-" + doctor.getId(), newAppTOReturn);
            messageSendingOperations.convertAndSend("/topic/appointmentReservation-" + patient.getId(), newAppTOReturn);

            if (appointment == null) return "failed, error taking an appointment !!";
            return "success, appointment has been created !!";
        } catch (Exception e) {
            System.out.println(e);
            return "failed:  " + e.getMessage();
        }
    }

    public String cancelAppointment(String appId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appId);
        int result = appointmentRepository.removeById(appId);

        if (appointment.isPresent() || result == 0) {
            Appointment app = appointment.get();
            int appsLength = appointmentRepository.getAppointmentsLengthByChatId(app.getChatId());
            if (appsLength == 0) chatRepository.deleteById(app.getChatId());


            return "success, appointment has been deleted !!";
        }
        return "failed, deleting the appointment";
    }


    public Object getDoctorWithAppDates(Long key) {
        try {
            Map<String, Object> doctor_dates = new HashMap<>();
            Doctor doctor = (Doctor) userRepository.findByJoinDate(key);
            Optional<List<Appointment>> appointments = appointmentRepository.findAllByDoctorId(doctor.getId());
            if (!appointments.isPresent()) return "failed, appointment not found";

            List<Object> appDates = new ArrayList<>(appointments.get().size());


            for (Appointment app : appointments.get()) {

                appDates.add(app.getAppointmentDate());
            }
            doctor_dates.put("appDates", appDates);
            doctor_dates.put("doctor", doctor);

            return doctor_dates;

        } catch (Exception e) {
            System.out.println(e);
            return "failed, " + e.getMessage();
        }

    }

    public Object findAllChats() {
        try {
            long starts = System.currentTimeMillis();
            String id = JwtService.extractGivenClaim("id").toString();
            String role = JwtService.extractGivenClaim("role").toString(); // 2 ms

            Optional<List<Appointment>> appointments = role.equalsIgnoreCase("doctor") ?
                    appointmentRepository.findAppointmentsByDoctor(id)
                    : appointmentRepository.findAppointmentsByPatient(id); // 200ms
            if (!appointments.isPresent()) return "failed, unable to get chats";
            Map<String, Map<String, Object>> chats = new HashMap<>();
            Map<String, String> connectedUsers = WebSocketEventListener.connectedUsers;


            List<String> chatIds = new ArrayList<>();
            for (Appointment app : appointments.get()) {

                String chatId = app.getChatId();

                Map<String, Object> itemInList = chats.get(chatId);

                if (itemInList == null) {
                    chatIds.add(chatId);
                    Optional<User> optionalUser = Role.DOCTOR.name().equals(role)
                            ? userRepository.findById(app.getPatientId()) : userRepository.findById(app.getDoctorId());
                    User user = optionalUser.get();
                    if (connectedUsers.containsValue(user.getId())) {

                    }
                    itemInList = new HashMap<>();
                    itemInList.put("connectionState", connectedUsers.containsValue(user.getId()) ? "online" : user.getLastSeen());
                    itemInList.put("user", filterUser(user));
                    itemInList.put("appointments", new ArrayList<>());
                    chats.put(chatId, itemInList);
                }

                ((List<Object>) itemInList.get("appointments")).add(filterApp(app));

            }

            List<Chat> chatList = chatRepository.findByIdIn(chatIds);
            for (Chat ch : chatList) {
                Map<String, Object> singleChat = chats.get(ch.getId());
                singleChat.put("chat", ch.getMessages());
                singleChat.put("unreadMessages", ch.getUnreadMessages().get(id));
                singleChat.put("chatId", ch.getId());
            }

            System.out.printf("Retrieving Chats takes %d ms%n", System.currentTimeMillis() - starts);
            return chats;
        } catch (Exception e) {
            System.out.println(e);
            return "error, " + e.getMessage();
        }
    }


    public Map<String, Object> filterApp(Appointment appointment) {
        Map<String, Object> appMap = new HashMap<>();
        appMap.put("appointmentDate", appointment.getAppointmentDate());
        appMap.put("duration", appointment.getDuration());
        appMap.put("cost", appointment.getCost());
        appMap.put("report", appointment.getReport());
        appMap.put("bookingDate", appointment.getKey());
        appMap.put("id", appointment.getId());
        return appMap;
    }

    public Object filterUser(User user) {

        User u = user;
        if (u instanceof Doctor) {
            Doctor d = (Doctor) u;
            int rating = 0;
            if (d.getRatings() != null) {
                Object o = d.getRatings().get(JwtService.extractGivenClaim("id"));
                if (o != null) rating = (Integer) o;
            }


            UserDTOut userDTOut = new UserDTOut(u.getFirstName(), u.getLastName(), u.getUsername()
                    , u.getEmail(), u.getId(), u.getBirthday(), u.getGender(), u.getPicture(), u.getRole().name().toLowerCase()
                    , u.getBio(), d.getProfession(), d.getCostPerAppointment(), d.getAppointmentDuration(), rating, d.getLastSeen());
            return userDTOut;

        } else {
            return new UserDTOut(u.getFirstName(), u.getLastName(), u.getUsername()
                    , u.getEmail(), u.getId(), u.getBirthday(), u.getGender(), u.getPicture(), u.getRole().name().toLowerCase()
                    , u.getBio(), null, null, null, null, u.getLastSeen());

        }

    }


    public Object findReport(Long key) {
        try {

            String role = JwtService.extractGivenClaim("role").toString();
            Criteria criteria = Criteria.where("key").is(key);
            Query query = new Query(criteria);

            Appointment app = mongoTemplate.findOne(query, Appointment.class);
            if (app == null) return "failed, appointment not found";

            Map userToReturn = filterUserWithFewInfo(app, role);
            Map appToReturn = filterApp(app);

            return Map.of("appointment", appToReturn, "user", userToReturn);

        } catch (Exception e) {
            System.out.println(e);
            return "failed, " + e.getMessage();
        }

    }

    public Object setReport(Long bookingDate, Report report) {
        try {
            Criteria criteria = Criteria.where("key").is(bookingDate);
            Query query = new Query(criteria);
            mongoTemplate.updateFirst(query, new Update().set("report", report), Appointment.class);
            return "success, report is added or updated";
        } catch (Exception e) {
            System.out.println(e);
            return "error, " + e.getMessage();
        }
    }

    private Map<String, Object> filterUserWithFewInfo(Appointment appointment, String role) {
        Optional<User> user = Role.DOCTOR.name().equals(role) ? userRepository.findById(appointment.getPatientId())
                : userRepository.findById(appointment.getDoctorId());
        User u = user.get();
        return getStringObjectMap(u);
    }

    private Map<String, Object> filterUserWithFewInfo(User u) {
        return getStringObjectMap(u);
    }

    @NonNull
    private Map<String, Object> getStringObjectMap(User u) {

        Map<String, Object> userMap = new HashMap<>();

        userMap.put("firstName", u.getFirstName());
        userMap.put("lastName", u.getLastName());
        userMap.put("id", u.getId());
        userMap.put("role", u.getRole());
        userMap.put("gender", u.getGender());
        userMap.put("picture", u.getPicture());

        if (u instanceof Doctor) {
            Doctor doctor = (Doctor) u;
            userMap.put("profession", doctor.getProfession());
        }
        return userMap;
    }

    public Object filterAppointmentsAccordingToState(List<Appointment> appointments, boolean isPending) {
        String role = JwtService.extractGivenClaim("role").toString();
        Map<String, Map<String, Object>> obj = new HashMap<>();
        List<String> usersId = new ArrayList<>();

        for (Appointment a : appointments) {
            String userId = Role.DOCTOR.name().equals(role) ? a.getPatientId() : a.getDoctorId();

            if (obj.containsKey(userId)) {
                Map<String, Object> alreadyExistUser = obj.get(userId);
                List<Object> updateListOfAppointments = (List<Object>) alreadyExistUser.get("appointments");
                updateListOfAppointments.add(filterApp(a));
            } else {
                usersId.add(userId);
                List<Object> newAppList = new ArrayList<>();
                newAppList.add(filterApp(a));
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("appointments", newAppList);
                obj.put(userId, userMap);
            }
        }
        List<User> users = userRepository.findAllById(usersId);
        for (User user : users) {
            Map<String, Object> updateObj = obj.get(user.getId());
            Object filteredUser = filterUserWithFewInfo(user);
            updateObj.put("user", filteredUser);
        }


        return obj;
    }


    public Object findAppointments(boolean isPending) {
        try {
            long startsTime = System.currentTimeMillis();

            String id = JwtService.extractGivenClaim("id").toString();
            String role = JwtService.extractGivenClaim("role").toString();
            System.out.println(id.equalsIgnoreCase("doctor"));
            Optional<List<Appointment>> appointments =
                    role.equalsIgnoreCase("doctor") ?
                            appointmentRepository.findAppointmentsByDoctor(id)
                            : appointmentRepository.findAppointmentsByPatient(id);
            System.out.println(appointments.get());
            if (!appointments.isPresent() || appointments.get().isEmpty()) return new ArrayList<>();
            Object o = filterAppointmentsAccordingToState(appointments.get(), isPending);

            System.out.printf("Retrieving appointments takes %d ms%n", System.currentTimeMillis() - startsTime);
            return o;
        } catch (Exception e) {
            System.out.println(e);
            return "error, " + e.getMessage();
        }


    }


    public Object getMedicalHistory(String patientId) {
        Optional<List<Appointment>> appointmentsOptional = appointmentRepository.findAppointmentsByPatient(patientId);

        if (appointmentsOptional.isPresent()) {
            List<Appointment> appointments = appointmentsOptional.get();
            // Collect all unique doctor IDs from the appointments
            Set<String> doctorIds = appointments.stream()
                    .map(Appointment::getDoctorId)
                    .collect(Collectors.toSet());

            // Fetch information for all doctors at once
            List<User> doctors = userRepository.findAllById(doctorIds);
            // Create a map of doctorId to User object for easy access
            Map<String, User> doctorMap = doctors.stream()
                    .collect(Collectors.toMap(User::getId, doctor -> doctor));

            List<Map<String, Object>> result = new ArrayList<>();

            // Build the result list of objects
            for (Appointment app : appointments) {
                if (app.getReport() != null && !app.getReport().fields.isEmpty()) {
                    Map<String, Object> entry = new HashMap<>();
                    Doctor d = (Doctor) doctorMap.get(app.getDoctorId());
                    entry.put("doctor", Map.of("profession", d.getProfession()));
                    entry.put("report", app.getReport().fields);
                    entry.put("appInfo", Map.of(
                            "duration", app.getDuration(),
                            "appointmentDate", app.getAppointmentDate()
                    ));
                    result.add(entry);
                }
            }


            System.out.println(result);
            return result;
        }
        return null;
    }
}
