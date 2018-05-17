/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model;

import static java.lang.System.out;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Wei.Cheng
 */
public class BABLoginStatusDAOTest {

    private String testJobnumber = "A-7568";

    public BABLoginStatusDAOTest() {
        BasicDAO.dataSourceInit();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getBABLoginStatus method, of class BABLoginStatusDAO.
     */
    @Test
    public void testBabLogin() {
//        out.println("testBabLogin");
//        BABLoginStatusDAO instance = new BABLoginStatusDAO();
//        boolean expResult = true;
//        boolean result = true;
//        for (int i = 3; i <= 8; i++) {
//            result = (result && instance.stationLogin(7, i, testJobnumber));
//        }
//        assertEquals(expResult, result);
    }

    /**
     * Test of getBABLoginStatus method, of class BABLoginStatusDAO.
     */
    @Test
    public void testBabLogout() {
        
//        out.println("testBabLogout");
//        BABLoginStatusDAO instance = new BABLoginStatusDAO();
//        boolean expResult = true;
//        boolean result = true;
//        for (int i = 3; i <= 8; i++) {
//            result = (result && instance.stationLogout(7, i));
//        }
//        assertEquals(expResult, result);
    }
}
