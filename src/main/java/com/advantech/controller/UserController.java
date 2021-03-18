/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 查看所屬人員是否存在
 */
package com.advantech.controller;

import com.advantech.datatable.DataTableResponse;
import com.advantech.helper.AtmcEmployeeUtils;
import com.advantech.helper.CustomPasswordEncoder;
import com.advantech.helper.SecurityPropertiesUtils;
import com.advantech.model.db1.User;
import com.advantech.service.db1.UserService;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private CustomPasswordEncoder pswEncoder;

    @Autowired
    private AtmcEmployeeUtils atmcEmployeeUtils;

    @RequestMapping(value = "/findAll", method = {RequestMethod.GET})
    @ResponseBody
    public DataTableResponse findAll() {
        return new DataTableResponse(userService.findAll());
    }

    @RequestMapping(value = "/findByFloor", method = {RequestMethod.GET})
    @ResponseBody
    public DataTableResponse findByFloor(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_OPER_IE")) {
            return new DataTableResponse(userService.findAll());
        } else {
            User user = SecurityPropertiesUtils.retrieveAndCheckUserInSession();
            return new DataTableResponse(userService.findByFloor(user.getFloor()));
        }
    }

    @RequestMapping(value = "/checkUser", method = {RequestMethod.GET})
    @ResponseBody
    public boolean checkUser(@RequestParam String jobnumber) {
        try {
            checkState(this.checkUserByAtmcRemote(jobnumber) == true, "User with jobnumber " + jobnumber + " not found");
            return true;
        } catch (Exception ex) {
            log.info("Have some issue on checking user on ATMC service...", ex);
            User user = userService.findByJobnumber(jobnumber);
            return user != null;
        }
    }

    public boolean checkUserByAtmcRemote(@RequestParam String jobnumber) throws Exception {
        String checkMsg = atmcEmployeeUtils.getUser(jobnumber);
        return !"".equals(checkMsg);
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
