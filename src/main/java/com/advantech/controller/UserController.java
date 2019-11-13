/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 查看所屬人員是否存在
 */
package com.advantech.controller;

import com.advantech.helper.CustomPasswordEncoder;
import com.advantech.helper.SecurityPropertiesUtils;
import com.advantech.model.User;
import com.advantech.model.view.UserInfoRemote;
import com.advantech.service.SqlViewService;
import com.advantech.service.UserService;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Wei.Cheng
 */
@Controller
@RequestMapping(value = "/UserController")
public class UserController {
    
    @Autowired
    private SqlViewService sqlViewService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CustomPasswordEncoder pswEncoder;

    @RequestMapping(value = "/checkUser", method = {RequestMethod.GET})
    @ResponseBody
    public boolean checkUser(@RequestParam String jobnumber) {
        return isUserExist(jobnumber);
    }

    private boolean isUserExist(String jobnumber) {
        //change the sql query(password not check)
        UserInfoRemote i = sqlViewService.findUserInfoRemote(jobnumber);
        return !(i == null);
    }
    
    @RequestMapping(value = "/updatePassword", method = {RequestMethod.POST})
    @ResponseBody
    public void updatePassword(@RequestParam String oldpassword, @RequestParam String password) {
        User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();
        checkState(user != null);
        
        String pswHash = pswEncoder.encode(oldpassword);
        checkArgument(user.getPassword().equals(pswHash), "Old password mismatch");
        
        String newPswHash = pswEncoder.encode(password);
        checkArgument(!pswHash.equals(newPswHash), "Password not changed");
        
        user.setPassword(newPswHash);
        userService.update(user);
    }

}