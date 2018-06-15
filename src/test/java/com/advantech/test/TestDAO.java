/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import com.advantech.dao.BabDAO;
import com.advantech.dao.BabPcsDetailHistoryDAO;
import com.advantech.dao.BabSettingHistoryDAO;
import com.advantech.dao.SensorTransformDAO;
import com.advantech.helper.CustomPasswordEncoder;
import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.model.Bab;
import com.advantech.model.BabSettingHistory;
import com.advantech.model.Line;
import com.advantech.model.SensorTransform;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
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
public class TestDAO {

    @Autowired
    private BabDAO babDAO;

    @Autowired
    private BabSettingHistoryDAO babSettingHistoryDAO;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private BabPcsDetailHistoryDAO babPcsDetailHistoryDAO;

    @Autowired
    private SensorTransformDAO sensorTransformDAO;

//    @Test
    @Transactional
    @Rollback(true)
    public void testBabDAO() throws JsonProcessingException {

        for (int i = 1; i <= 4; i++) {
            List l = babDAO.findUnReplyed(i);
            assertTrue(l.isEmpty());
        }

    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testBabDAO2() throws JsonProcessingException, ParseException {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date today = ft.parse("2017-12-21 00:00:00");
        BabSettingHistory setting = babSettingHistoryDAO.findByPrimaryKey(18893);
        assertNotNull(setting);
        setting.setJobnumber("A-7567");
        babSettingHistoryDAO.update(setting);
        assertNotNull(setting.getLastUpdateTime());
        assertTrue(setting.getLastUpdateTime().after(today));
        HibernateObjectPrinter.print(setting);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testBabPcsDetailHistoryDAO() throws JsonProcessingException, ParseException {
        List<Map> l = babPcsDetailHistoryDAO.findByBabForMap(5949);
        HibernateObjectPrinter.print(l);
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testHibernateInitialize() {
        Session session = sessionFactory.getCurrentSession();

        Line line = new Line();
        line.setId(1);

        Bab b = new Bab("test", "test", line, 3, 1);
        session.save(b);
        assertTrue(b.getId() != 0);

        HibernateObjectPrinter.print(b);

        session.flush();

        Hibernate.initialize(b.getLine());

        assertTrue(line.getName() != null);

        HibernateObjectPrinter.print(line);
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testFindWithBestBalanceAndSetting() {
        List l = babSettingHistoryDAO.findAll("PSH5579ZA", 0, true, false, 0);
        assertTrue(l != null);
        assertTrue(!l.isEmpty());
        HibernateObjectPrinter.print(l);
    }

}
