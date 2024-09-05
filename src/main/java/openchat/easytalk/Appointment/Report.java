package openchat.easytalk.Appointment;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor

public class Report {

    List<Field> fields;

    Long lastModify;

    Report() {
        fields = new ArrayList<>();
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @AllArgsConstructor
    @ToString
    @NoArgsConstructor
    @Data
    public static class Field {
        String title;
        String body;
        String visibility;

    }


}
