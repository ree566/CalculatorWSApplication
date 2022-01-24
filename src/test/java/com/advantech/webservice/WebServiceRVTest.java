/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.webservice;

import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.model.db1.PassStationRecord;
import com.advantech.model.db1.TestPassStationDetail;
import com.advantech.model.db1.TestRecord;
import com.advantech.model.db1.UserOnMes;
import com.advantech.service.db1.TestRecordService;
import com.advantech.service.db1.TestService;
import com.advantech.webservice.mes.Section;
import com.fasterxml.jackson.core.JsonProcessingException;
import static com.google.common.collect.Lists.newArrayList;
import com.google.gson.Gson;
import static java.lang.System.out;
import java.util.List;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
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
public class WebServiceRVTest {

    @Autowired
    private WebServiceRV rv;

    public WebServiceRVTest() {
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
     * Test of getKanbanUsersForString method, of class WebServiceRV.
     */
//    @Test
    public void testGetKanbanUsersForString() throws Exception {
        System.out.println("getKanbanUsersForString");
        List<String> result = rv.getKanbanUsersForString(Factory.DEFAULT);
        assertTrue(!result.isEmpty());
    }

    /**
     * Test of getKanbanWorkId method, of class WebServiceRV.
     */
//    @Test
    public void testGetKanbanWorkId() throws Exception {
        System.out.println("getKanbanWorkId");
        String jobnumber = "A-7275";
        String expResult = "";
        String result = rv.getKanbanWorkId(jobnumber, Factory.DEFAULT);
        assertNotEquals(expResult, result);
        out.println(result);
    }

    /**
     * Test of getModelnameByPo method, of class WebServiceRV.
     */
//    @Test
    public void testGetModelnameByPo() throws Exception {
        System.out.println("getModelnameByPo");
        String po = "PAGB079ZA";
        String expResult = "";
        String result = rv.getModelNameByPo(po, Factory.DEFAULT);
        assertNotEquals(expResult, result);
        out.println(result);
    }

    /**
     * Test of getMESUser method, of class WebServiceRV.
     */
//    @Test
    public void testGetMESUser() {
        System.out.println("getMESUser");
        String jobnumber = "A-7275";
        UserOnMes expResult = null;
        UserOnMes result = rv.getMESUser(jobnumber, Factory.DEFAULT);
        assertNotEquals(expResult, result);
        out.println(new Gson().toJson(result));
    }

    /**
     * Test of getPassStationRecords method, of class WebServiceRV.
     */
//    @Test
    public void testGetPassStationRecords() {
        System.out.println("getPassStationRecords");
        String po = "THL007939ZA";
        List<PassStationRecord> result = rv.getPassStationRecords(po, 2, Factory.DEFAULT);
        assertTrue(!result.isEmpty());
        HibernateObjectPrinter.print(result);
    }

    /**
     * Test of getTestLineTypeUsers method, of class WebServiceRV.
     */
//    @Test
    public void testGetTestLineTypeUsers() {
        System.out.println("getTestLineTypeUsers");
        List<TestRecord> expResult = null;
        List<TestRecord> result = rv.getTestLineTypeRecords(Factory.DEFAULT);
        assertNotEquals(expResult, result);
        for (TestRecord t : result) {
            out.println(new Gson().toJson(t));
        }
    }

//    @Test
    public void testGetTestLineTypeRecord() throws JsonProcessingException {
        List<TestRecord> l = rv.getTestLineTypeRecords(Factory.DEFAULT);
        assertNotEquals(0, l.size());
        HibernateObjectPrinter.print(l);
    }

//    @Test
    public void testGetModelNameByBarcode() throws JsonProcessingException {
        String value = rv.getPoByBarcode("TPAB780972", Factory.DEFAULT);
        assertEquals(value, "PSI9412ZA");
    }

//    @Test
    public void testGetMesPassCountRecords() {
        DateTime eD = new DateTime("2021-04-29");
        DateTime sD = eD.minusDays(1);
        List l = rv.getMesPassCountRecords(sD, eD, Factory.DEFAULT);
        assertTrue(!l.isEmpty());
        HibernateObjectPrinter.print(l);
    }

    @Autowired
    private TestService testService;
    
    @Autowired
    private TestRecordService testRecordService;

    @Test
    public void testGetTestPassStationDetails() {
        DateTime eD = new DateTime().withTime(8, 0, 0, 0);
        DateTime sD = eD.minusDays(2).withTime(8, 0, 0, 0);
        List<String> users = newArrayList("'A-9043'");

        List<TestRecord> records = testRecordService.findByDate(sD, eD, false);
        List<String> jobnumbers = records.stream().map(t -> "'" + t.getUserId() + "'").distinct().collect(Collectors.toList());
        List<Integer> stations = newArrayList(3, 11, 30, 151);

        stations.forEach(s -> {
            Section section = (s == 3 ? Section.BAB : Section.TEST);
            List<TestPassStationDetail> l = rv.getTestPassStationDetails(users, section, s, sD, eD, Factory.TEMP1);
            //assertTrue(!l.isEmpty());
            HibernateObjectPrinter.print(l);
        });
    }

//    @Test
    public void testGetUsersInfoOnMes() {
        List l = rv.getUsersInfoOnMes(Factory.DEFAULT);
        assertTrue(!l.isEmpty());

        HibernateObjectPrinter.print(l.get(0));
    }

//    @Test
    public void testRptStationQtys() {
        DateTime sD = new DateTime().withTime(0, 0, 0, 0);
        DateTime eD = new DateTime().plusDays(1).withTime(0, 0, 0, 0);

        List l = rv.getRptStationQtys("EKI-1524I-CE", 2, Factory.DEFAULT);
        assertTrue(!l.isEmpty());

        HibernateObjectPrinter.print(l.get(0));
    }
}
