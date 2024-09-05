package openchat.easytalk.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateProvider {


    public static Instant parse(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(date).toInstant();
    }

    public static List<Instant> parseAll(List<String> dates) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        List<Instant> instants = new ArrayList<>(dates.size());
        for (String date : dates) {
            instants.add(simpleDateFormat.parse(date).toInstant());
        }
        return instants;
    }
}
