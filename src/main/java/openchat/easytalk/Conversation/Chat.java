package openchat.easytalk.Conversation;

import lombok.Data;
import lombok.NonNull;
import openchat.easytalk.User.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "conversations")
public class Chat {

    @Id
    @Indexed
    String id;

    @NonNull
    @Indexed
    private String friend1Id;

    @NonNull
    @Indexed
    private String friend2Id;

    @NonNull
    @Indexed(unique = true)
    private String key;

    private List<Message> messages;
    private Map<String, Integer> unreadMessages;


    public Chat(@NonNull String friend1Id, @NonNull String friend2Id, String key) {
        this.friend1Id = friend1Id;
        this.friend2Id = friend2Id;
        this.messages = new ArrayList<>();
        this.key = key;
        unreadMessages = new HashMap<>();
        unreadMessages.put(friend1Id, 0);
        unreadMessages.put(friend2Id, 0);
    }
}
