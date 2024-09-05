package openchat.easytalk;

import lombok.EqualsAndHashCode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.FilterOutputStream;

@SpringBootApplication
public class EasyTalkApplication {


    public static void main(String[] args) {

        SpringApplication.run(EasyTalkApplication.class, args);


    }

}

