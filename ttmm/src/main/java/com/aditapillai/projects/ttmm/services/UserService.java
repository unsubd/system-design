package com.aditapillai.projects.ttmm.services;

import com.aditapillai.projects.ttmm.dao.UserDao;
import com.aditapillai.projects.ttmm.exceptions.ApiException;
import com.aditapillai.projects.ttmm.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserDao dao;

    /**
     * Find user by id
     *
     * @param id of the user
     * @return the correct user matched by the id
     * @throws ApiException with status as 404 if the user is not found
     */
    public User findById(String id) {
        return this.dao.findUserById(id)
                       .orElseThrow(() -> new ApiException(404, String.format("User with id: %s not found", id)));
    }

    /**
     * Find user by id
     *
     * @param email of the user
     * @return the correct user matched by the id
     * @throws ApiException with status as 404 if the user is not found
     */
    public User findUserByEmail(String email) {
        return this.dao.findUserByEmail(email)
                       .orElseThrow(() -> new ApiException(404, String.format("User with email: %s not found", email)));
    }

    /**
     * Find user by id
     *
     * @param phone number of the user
     * @return the correct user matched by the id
     * @throws ApiException with status as 404 if the user is not found
     */
    public User findUserByPhone(String phone) {
        return this.dao.findUserByPhone(phone)
                       .orElseThrow(() -> new ApiException(404, String.format("User with phone: %s not found", phone)));
    }

    /**
     * Save the user to the db
     *
     * @param user to be saved
     * @return saved user
     */
    public User save(User user) {
        return this.dao.save(user);
    }

    /**
     * Find all the friends for the given user
     *
     * @param userId of the user whose friends need to be fetched
     * @return list of friends
     */
    public List<User> findFriendsForUserId(String userId) {
        return this.dao.findFriendsForUserId(userId);
    }

    /**
     * Add a friend in the given users friendlist
     *
     * @param user   to be updated
     * @param friend to be added
     * @return updated user
     */
    public User addFriend(User user, User friend) {
        return this.dao.addFriend(user, friend);
    }


    @Autowired
    public void setDao(UserDao dao) {
        this.dao = dao;
    }
}
