package openchat.easytalk.Conversation;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {


    @Override
    List<Chat> findAll();

    @Query(value = "{key: ?0}")
    Optional<Chat> findByKey(String key);

    @Query(value = "{_id: ?0}")
    Optional<Chat> findById(String id);


    @Query(value = "{ _id : { $in: ?0 } }")
    List<Chat> findByIdIn(List<String> ids);

}
