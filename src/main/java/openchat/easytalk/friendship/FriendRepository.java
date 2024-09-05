package openchat.easytalk.friendship;

import openchat.easytalk.User.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends MongoRepository<User, String> {

    @Query(value = "{$and: [{username: ?0},{username.friends.username: ?1}]}")
    Optional<User> getFriend(String username, String friendUsername);


}
