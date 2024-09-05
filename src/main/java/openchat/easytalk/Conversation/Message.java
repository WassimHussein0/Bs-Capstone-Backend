package openchat.easytalk.Conversation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Message {

    private String body;
    private long sent;
    private String sender;

}
