/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.webservice;

//import com.advantech.webservice.WebServiceTX;
import static java.lang.System.out;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class WebServiceTXTest {
    
    @Autowired
    private WebServiceTX tx;

    String testJobnumber = "A-7568";

    //測試登入登出請距離超過30Min
    /**
     * Test of getMESUser method, of class WebServiceRV.
     */
    @Test
    public void testLogin() {
        out.println("testLogin");
        tx.kanbanUserLogin(testJobnumber);
    }
    /**
     * Test of getMESUser method, of class WebServiceRV.
     */
    @Test
    public void testLogout() {
        out.println("testLogout");
        tx.kanbanUserLogout(testJobnumber);
    }

}
