/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.UserDAO;
import com.advantech.helper.CustomPasswordEncoder;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Unit;
import com.advantech.model.db1.User;
import com.advantech.model.db1.UserProfile;
import com.advantech.security.State;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private CustomPasswordEncoder passwordEncoder;
    
    @Autowired
    private FloorService floorService;
    
    @Autowired
    private UnitService unitService;
    
    @Autowired
    private UserProfileService userProfileService;

    public List<User> findAll() {
        return userDAO.findAll();
    }

    public User findByPrimaryKey(Object obj_id) {
        return userDAO.findByPrimaryKey(obj_id);
    }

    public User findByJobnumber(String jobnumber) {
        User i = userDAO.findByJobnumber(jobnumber);

        if (i == null) {
            return null;
        }

        //Initialize the lazy loading relative object
        Hibernate.initialize(i.getUnit());
        Hibernate.initialize(i.getFloor());
        Hibernate.initialize(i.getUserProfiles());
        return i;
    }

    public List<User> findLineOwner(int line_id) {
        return userDAO.findLineOwner(line_id);
    }

    public List<User> findLineOwnerBySitefloor(int floor_id) {
        return userDAO.findLineOwnerBySitefloor(floor_id);
    }

    public List<User> findByUserNotification(String notification_name) {
        return userDAO.findByUserNotification(notification_name);
    }

    public List<User> findByUserNotificationAndNotLineOwner(String notification_name) {
        return userDAO.findByUserNotificationAndNotLineOwner(notification_name);
    }

    public List<User> findByUserNotificationAndNotLineOwner(int floor_id, String notification_name) {
        return userDAO.findByUserNotificationAndNotLineOwner(floor_id, notification_name);
    }

    public List<User> findByFloor(Floor f) {
        return userDAO.findByFloor(f);
    }

    public List<User> findByRole(String... roleName) {
        return userDAO.findByRole(roleName);
    }

    public List<User> findByFloorAndRole(Floor f, String... roleName) {
        return userDAO.findByFloorAndRole(f, roleName);
    }

    public List<User> findByState(State state) {
        return userDAO.findByState(state);
    }

    public int insert(User pojo) {
        return userDAO.insert(pojo);
    }
    
    public int insert(String jobnumber) {
        User user = new User();
        user.setJobnumber(jobnumber);
        user.setUsername(jobnumber);
        user.setUsernameCh(jobnumber);
        user.setPassword(this.passwordEncoder.encode(jobnumber));
        user.setState(State.ACTIVE);
        
        Unit defaultUnit = unitService.findByPrimaryKey(1);
        Floor defaultFloor = floorService.findByPrimaryKey(1);
        
        user.setUnit(defaultUnit);
        user.setFloor(defaultFloor);
        
        UserProfile defaultUserProfile = userProfileService.findByPrimaryKey(3);
        Set<UserProfile> s = new HashSet<>(1);
        s.add(defaultUserProfile);
        user.setUserProfiles(s);
        
        return userDAO.insert(user);
    }

    public int update(User pojo) {
        return userDAO.update(pojo);
    }

    public int delete(User pojo) {
        return userDAO.delete(pojo);
    }

}
