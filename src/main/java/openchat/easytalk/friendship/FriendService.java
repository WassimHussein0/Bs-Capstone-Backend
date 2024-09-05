package openchat.easytalk.friendship;

import lombok.RequiredArgsConstructor;
import openchat.easytalk.User.User;
import openchat.easytalk.User.UserRepository;
import openchat.easytalk.User.UserService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service

public class FriendService {

    final UserRepository userRepository;
    final MongoTemplate mongoTemplate;
    final UserService userService;
    final FriendRepository friendRepository;

    public String addFriend(String friendUsername, String username) {

        Optional<User> friend = userRepository.findByUniqueIdentifier(username);
        if (friend.isPresent() && !friendUsername.equals(username)) {
            mongoTemplate.upsert(
                    Query.query(Criteria.where("username").is(username)),
                    new Update().push("friends").value(friend.get()),
                    User.class
            );
            return "success, friend is added";
        } else return //isFriend.isPresent() ? "failed, friend already exist"
                friend.isPresent() ? "failed, adding yourself as a friend"
                        : "failed, adding someone who does not exist";
    }
}
