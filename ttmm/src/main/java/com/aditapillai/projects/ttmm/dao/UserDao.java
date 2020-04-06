package com.aditapillai.projects.ttmm.dao;

import com.aditapillai.projects.ttmm.models.User;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDao {
    private MongoOperations mongoOperations;

    /**
     * Find the user by phone
     *
     * @param id of the user
     * @return Optional User if found, else return an empty Optional
     */
    public Optional<User> findUserById(String id) {
        return Optional.ofNullable(this.mongoOperations.findById(id, User.class));
    }

    /**
     * Find the user by phone
     *
     * @param email of the user
     * @return Optional User if found, else return an empty Optional
     */
    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(this.mongoOperations.findOne(new Query(Criteria.where("email")
                                                                                  .is(email)), User.class));
    }

    /**
     * Find the user by phone
     *
     * @param phone number of the user
     * @return Optional User if found, else return an empty Optional
     */
    public Optional<User> findUserByPhone(String phone) {
        return Optional.ofNullable(this.mongoOperations.findOne(new Query(Criteria.where("phone")
                                                                                  .is(phone)), User.class));
    }

    /**
     * Save the user to the db
     *
     * @param user to be saved
     * @return saved user
     */
    public User save(User user) {
        return this.mongoOperations.save(user);
    }

    /**
     * Fetch the list of friends for the given user id
     *
     * @param userId whose friend list needs to be fetched
     * @return list of friends of the user
     */
    public List<User> findFriendsForUserId(String userId) {
        return this.findUserById(userId)
                   .map(user -> this.mongoOperations.find(new Query(Criteria.where("_id")
                                                                            .in(user.getFriends())), User.class))
                   .orElse(new LinkedList<>());


    }

    /**
     * Add a friend for the given user
     *
     * @param user   to be updated
     * @param friend friend to be added
     * @return updated user
     */
    public User addFriend(User user, User friend) {
        UpdateResult updateResult = this.mongoOperations.updateFirst(new Query(Criteria.where("_id")
                                                                                       .is(user.getId())),
                new Update().push("friends", friend.getId()), User.class);
        user.getFriends()
            .add(friend.getId());
        return user;
    }

    @Autowired
    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
}
