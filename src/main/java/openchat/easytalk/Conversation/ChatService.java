package openchat.easytalk.Conversation;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import openchat.easytalk.Config.Jwt.JwtService;
import openchat.easytalk.GenericDTOs.ListDTO;
import openchat.easytalk.Time.DateProvider;
import openchat.easytalk.Tools.StringServices;
import openchat.easytalk.User.User;
import openchat.easytalk.User.UserRepository;
import openchat.easytalk.User.UserService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService implements IMessagesServices {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;


    public Chat findChat(String key) {
        Optional<Chat> chat = chatRepository.findByKey(key);
        return chat.isPresent() ? chat.get() : null;
    }

    public Chat findOrCreateChat(User u1, User u2) {
        try {
            String key = StringServices.combinedInOrder(u1.getId(), u2.getId());
            Optional<Chat> tryGet = chatRepository.findByKey(key);
            if (tryGet.isPresent()) return tryGet.get();
            return chatRepository.save(new Chat(u1.getId(), u2.getId(), key));
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    public Object findOrCreate(String friendId) {
        String myId = (String) JwtService.extractGivenClaim("id");

        Chat ch = findChat(StringServices.combinedInOrder(myId, friendId));
        if (ch != null)
            return ch.getMessages();

        if (myId.equals(friendId))
            return "failed, friend username is the same of the caller";
        List<User> f = userRepository.findAllById
                (List.of(friendId, myId));
        if (f.size() == 2) {
            System.out.println("Success");
            chatRepository.insert(new Chat(f.get(0).getId(), f.get(1).getId(),
                    StringServices.combinedInOrder(myId, friendId)));
            return "success, chat has been created between "
                    + f.get(0).getUsername() + " and " + f.get(1).getUsername();
        } else {
            return "failed,  parameter is not correct";

        }
    }

    public Chat findById(String id) {
        Optional<Chat> chat = chatRepository.findById(id);
        return chat.isPresent() ? chat.get() : null;
    }

    public void setUnreadMessages(String chatId, String userId, boolean increment) {
        Query query = new Query(Criteria.where("_id").is(chatId));
        Update update;
        if (increment) {
            update = new Update().inc("unreadMessages." + userId, 1);
        } else {
            update = new Update().set("unreadMessages." + userId, 0);
        }

        mongoTemplate.updateFirst(query, update, "conversations");
    }


    public boolean add(String username, Message message) {
        try {
            String join = StringServices.combinedInOrder(username, message.getSender());
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("key").is(join)),
                    new Update().push("messages").value(message),
                    Chat.class
            );
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean addAll(String friend, List<Message> messages) {
        return false;
    }

    public boolean deleteMessages(String username, @RequestBody ListDTO<String> dates) {
        try {
            List<String> instants = dates.getValues();
            String join = StringServices.combinedInOrder(username, JwtService.extractUsername());
            Query query = Query.query(Criteria.where("compoundName").is(join));
            Update update = new Update().pull("messages",
                    Query.query(Criteria.where("sent").in(DateProvider.parseAll(instants))));
            UpdateResult result = mongoTemplate.updateFirst(query, update, Chat.class);

            if (result.getModifiedCount() > 0) {
                // At least one document was updated, which means a message was deleted
                return true;
            } else {
                // No documents were updated, meaning the message was not found
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            return false;
        }
    }


}
