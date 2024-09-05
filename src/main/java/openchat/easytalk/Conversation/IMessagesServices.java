package openchat.easytalk.Conversation;

import openchat.easytalk.GenericDTOs.ListDTO;

import java.util.List;

public interface IMessagesServices {

    public Object findOrCreate(String friend);

//    boolean add(String userId, Message message);

    boolean addAll(String user1Id, List<Message> messages);

    boolean deleteMessages(String user1Id, ListDTO<String> dates);


}
