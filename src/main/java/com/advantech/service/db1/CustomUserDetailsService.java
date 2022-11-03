/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.User;
import com.advantech.security.State;
import com.advantech.webservice.atmc.AtmcEmployeeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Wei.Cheng
 */
@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private AtmcEmployeeUtils employeeUtils;

    @Override
    public UserDetails loadUserByUsername(String jobnumber) throws UsernameNotFoundException {
        User user = userService.findByJobnumber(jobnumber);
        if (user == null) {
            if (!isUserExistInEmployeeZone(jobnumber)) {
                System.out.println("User not found");
                throw new UsernameNotFoundException("Username not found");
            }
            userService.insert(jobnumber);
            user = userService.findByJobnumber(jobnumber);
        }

        user.addSecurityInfo(user.getState() == State.ACTIVE, true, true, true, getGrantedAuthorities(user));
        return user;
    }

    private List<GrantedAuthority> getGrantedAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        user.getUserProfiles().forEach((userProfile) -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userProfile.getName()));
        });

        System.out.println("authorities :" + authorities);
        return authorities;
    }

    private boolean isUserExistInEmployeeZone(String jobnumber) {
        try {
            return !"".equals(employeeUtils.getUser(jobnumber));
        } catch (Exception ex) {
            return false;
        }
    }

}
