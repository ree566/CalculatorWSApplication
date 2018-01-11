/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import com.advantech.dao.BabDAO;
import com.advantech.dao.BabPcsDetailHistoryDAO;
import com.advantech.dao.BabSettingHistoryDAO;
import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.model.Bab;
import com.advantech.model.BabSettingHistory;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @Test
    @Transactional
    @Rollback(true)
    public void testBabDAO() throws JsonProcessingException {
//        Bab b = babDAO.findByPrimaryKey(11262);
//        assertNotNull(b);
//        BabSettingHistory setting = new BabSettingHistory(b, 2, b.getLine().getName() + "-S-" + 2, "A-TEST");
//        babSettingHistoryDAO.insert(setting);
//        assertNotNull(setting.getCreateTime());
//        HibernateObjectPrinter.print(setting);
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
        List<Map> l = babPcsDetailHistoryDAO.findByBabForMap(12597);
        assertNotEquals(0, l.size());
        Map m = l.get(0);
        assertNotNull(m.get("id"));
        HibernateObjectPrinter.print(l);
    }

}
