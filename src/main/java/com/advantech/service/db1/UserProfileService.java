/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.UserProfileDAO;
import com.advantech.model.db1.UserProfile;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class UserProfileService {

    @Autowired
    private UserProfileDAO dao;

    public List<UserProfile> findAll() {
        return dao.findAll();
    }

    public UserProfile findByPrimaryKey(Object obj_id) {
        return dao.findByPrimaryKey(obj_id);
    }

    public int insert(UserProfile pojo) {
        return dao.insert(pojo);
    }

    public int update(UserProfile pojo) {
        return dao.update(pojo);
    }

    public int delete(UserProfile pojo) {
        return dao.delete(pojo);
    }

}
