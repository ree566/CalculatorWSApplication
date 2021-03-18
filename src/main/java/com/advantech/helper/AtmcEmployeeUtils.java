/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import static com.google.common.collect.Maps.newHashMap;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author MFG.ESOP
 */
@Component
public class AtmcEmployeeUtils {
    
    private final String CHARSET = "UTF-8";
    
    @Value("${atmc.employee.userQry.url}")
    private String userQryUrl;
    
    @Value("${atmc.employee.userLogin.url}")
    private String userLoginUrl;

    public String getUser(String jobnumber) throws Exception {
        String url = userQryUrl + jobnumber;
        String result = HttpClientUtil.doGet(url, newHashMap(), CHARSET);
        return result;
    }

    public boolean userLogin(String jobnumber, String password) throws Exception {
        String url = userLoginUrl;
        Map m = new HashMap();
        m.put("empNo", jobnumber);
        m.put("password", password);
        String result = HttpClientUtil.doPost(url, m, CHARSET);
        return Boolean.parseBoolean(result);
    }
}
