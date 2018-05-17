/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model;

import static java.lang.System.out;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Wei.Cheng
 */
public class CountermeasureDAOTest {

    public CountermeasureDAOTest() {
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
     * Test of getCountermeasureView method, of class CountermeasureDAO.
     */
    @Test
    public void testGetCountermeasureView() {

        //        System.out.println("getCountermeasureView");
//        CountermeasureDAO instance = new CountermeasureDAO();
//        List<BAB> notExpResult = null;
//        List<BAB> notExpResult2 = new ArrayList();
//
////        List<Map> result = instance.getCountermeasureView("16/09/08", "16/09/08");
//        List<BAB> result = instance.getUnFillCountermeasureBabs("5");
//
//        assertNotEquals(notExpResult, result);
//        assertNotEquals(notExpResult2, result);
//
//        for (BAB m : result) {
//            System.out.println(m.getPO());
//        }
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

        out.println(request.getRemoteAddr());

    }
}
