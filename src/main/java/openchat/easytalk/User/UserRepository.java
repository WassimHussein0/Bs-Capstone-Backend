package openchat.easytalk.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);


    @Query(value = "{username: ?0}", exists = true)
    boolean existsByUsername(String username);

    @Query(value = "{email: ?0}", exists = true)
    boolean existsByEmail(String email);

    @Query(value = "{$or: [ {username: ?0}, {email: ?0} ]}")
    Optional<User> findByUniqueIdentifier(String username);

    @Query(value = "{$or: [ {username: ?0}, {email: ?0} ]}",
            fields = "{_id:  0, username: 1, email:  1, gender:  1, address: 1}")
    Optional<Object> find(String username);


    @Query(value = "{_id:  ?0}",
            fields = "{ id: { '$toString': '$_id' },'role':1, 'firstName' : 1, 'profession': 1," +
                    " 'lastName' : 1,'username': 1,'bio':  1,'email':1, 'birthday':1,'joinDate':1," +
                    " 'picture': 1, 'gender': 1 ,'costPerAppointment': 1, 'appointmentDuration':  1, " +
                    "'rating':  1, 'schedule':  1, 'ratings':  1}"
    )
    Optional<Object> findUserById(String id);

    @Query(value = "{?0:  ?1}",
            fields = "{ id: { '$toString': '$_id' },'role':1, 'firstName' : 1, 'profession': 1, 'lastName' : 1,'username': 1,'bio':  1,'email':1, 'birthday':1,'joinDate':1, 'picture': 1, 'gender': 1 ,'costPerAppointment': 1, 'appointmentDuration':  1, 'rating':  1, 'schedule':  1, 'isApproved':  1}"
    )
    Optional<List<Object>> findAllBy(String filedName, String role);

    @Query(value = "{ $and: [ { ?0: ?1 }, { ?2: ?3 } ] }",
            fields = "{ id: { '$toString': '$_id' },joinDate:1, 'firstName' : 1, 'profession': 1, 'lastName' : 1,'username': 1,'bio':  1,'email':1, 'birthday':1, 'picture': 1, 'gender': 1 ,'costPerAppointment': 1, 'appointmentDuration':  1, 'ratings':  1, 'schedule':  1, }"
    )
    Optional<List<Object>> findAllByTwoFieldNames(String filedName1, String value1, String filedName2, boolean value2);

    @Query(value = "{username: {$in:  ?0} }",
            fields = "{firstName: 1, lastName: 1, username: 1, email: 1, address: 1 , _id:  0}")
    List<Object> findAllByUsernameWith(List<String> users);

    @Query(value = "{}", fields = "{ 'firstName' : 1, 'lastName' : 1,'username': 1,'bio':  1, '_id' : 0 }",
            sort = "{'firstName': 1, 'lastName': 1}")
    List<Object> findAllWith();


    @Query(value = "{joinDate: {$in:  ?0} }")
    List<User> findAllByJoinDate(List<Long> users);

    @Query(value = "{_id: {$in:  ?0 }}")
    List<User> findAllById(List<String> users);

    @Query(value = "{joinDate:   ?0}")
    User findByJoinDate(Long key);

    @Query(value = "{_id:  ?0}", delete = true)
    Optional<Boolean> delete(String id);

    @Query(value = "{username: ?0}", fields = "{'_id.$oid': 1}")
    String getId(String username);


}
