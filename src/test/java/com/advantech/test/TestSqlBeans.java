/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import com.advantech.dao.db1.LineDAO;
import com.advantech.dao.db1.LineUserReferenceDAO;
import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabAlarmHistory;
import com.advantech.model.db1.BabLineProductivityExclude;
import com.advantech.model.db1.BabLineProductivityExcludeModel;
import com.advantech.model.db1.BabPassStationRecord;
import com.advantech.model.db1.BabStandardTimeHistory;
import com.advantech.model.db1.Countermeasure;
import com.advantech.model.db1.CountermeasureType;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Fqc;
import com.advantech.model.db1.FqcLine;
import com.advantech.model.db1.FqcLoginRecord;
import com.advantech.model.db1.FqcModelStandardTime;
import com.advantech.model.db1.FqcSettingHistory;
import com.advantech.model.db1.Line;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.LineUserReference;
import com.advantech.model.db1.ModelSopRemark;
import com.advantech.model.db1.ModelSopRemarkDetail;
import com.advantech.model.db1.PreAssyModuleStandardTime;
import com.advantech.model.db1.PreAssyModuleType;
import com.advantech.model.db1.PrepareSchedule;
import com.advantech.model.db1.TestPassStationDetail;
import com.advantech.model.db1.TestTable;
import com.advantech.model.db1.Unit;
import com.advantech.model.db1.User;
import com.advantech.model.db1.Worktime;
import com.advantech.service.db1.TestPassStationDetailService;
import com.advantech.webservice.Factory;
import com.advantech.webservice.Section;
import com.advantech.webservice.WebServiceRV;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import javax.transaction.Transactional;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import org.junit.Before;
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
@Transactional
//@Rollback(true)
public class TestSqlBeans {

    @Autowired
    SessionFactory sessionFactory;

    Session session;

    @Before
    public void init() {
        session = sessionFactory.getCurrentSession();
    }

//    @Test
    public void testFloor() {
        assertNotNull(session.get(Floor.class, 1));
    }

//    @Test
    public void testUser() {
        User user = (User) session.get(User.class, 1);
        assertNotNull(user);
        assertEquals(1, user.getUserProfiles().size());
        assertEquals(1, user.getUserNotifications().size());
        Unit u = user.getUnit();
        assertNotNull(u);
        assertEquals("MFG", u.getName());
    }

//    @Test
    public void testTestLine() {
        TestTable table = (TestTable) session.get(TestTable.class, 1);
        assertNotNull(table);
        assertEquals("5", table.getFloor().getName());
        assertEquals(1, table.getTests().size());
        assertEquals(1482, table.getTestLineTypeRecords().size());
    }

//    @Test
    public void testBabLine() {
        Line line = (Line) session.get(Line.class, 1);
        assertNotNull(line);
        LineType lineType = line.getLineType();
        assertNotNull(lineType);
        assertEquals("ASSY", lineType.getName());
        assertEquals(2, lineType.getLineTypeConfigs().size());
        assertEquals(955, line.getBabs().size());
        Bab bab = (Bab) session.get(Bab.class, 10790);
        assertNotNull(bab);
        assertEquals(20, bab.getFbns().size());
        assertEquals(8, bab.getBabPcsDetailHistorys().size());
        assertEquals(2, bab.getBabSettingHistorys().size());
        assertEquals(4, bab.getBabBalanceHistorys().size());
        assertEquals(0, bab.getBabAlarmHistorys().size());
        BabAlarmHistory bAlarm = (BabAlarmHistory) session.get(BabAlarmHistory.class, 6049);
        assertEquals(4, bAlarm.getFailPcs());
        Set<Countermeasure> s = bab.getCountermeasures();
        assertNotEquals(0, s.size());
        Countermeasure cm = s.stream().findFirst().get();
        assertEquals(1, cm.getCountermeasureEvents().size());
        assertEquals(1, cm.getErrorCodes().size());
        assertEquals(1, cm.getActionCodes().size());
    }

//    @Test
    public void testConverter() {
        User user = session.get(User.class, 1);
        assertNotNull(user);
        HibernateObjectPrinter.print(user);
        Bab b = session.get(Bab.class, 13091);
        assertNotNull(b);
        HibernateObjectPrinter.print(b);
    }

//    @Test
    public void testFqcConverter() {
//        FqcLine fqcLine = session.get(FqcLine.class, 1);
//        assertNotNull(fqcLine);
//        assertEquals(Factory.DEFAULT, fqcLine.getFactory());

        List l = session.createCriteria(FqcLine.class).add(Restrictions.eq("factory", Factory.TEMP1)).list();
        assertTrue(!l.isEmpty());
        HibernateObjectPrinter.print(l);
    }

//    @Test
//    @Rollback(false)
    public void testLogin() {
        FqcLine fqcLine = session.get(FqcLine.class, 1);
        FqcLoginRecord loginRecord = new FqcLoginRecord(fqcLine, "A-Test");
        session.save(loginRecord);
        assertTrue(loginRecord.getId() != 0);
        System.out.println("Login record's id: " + loginRecord.getId());

        Fqc fqc = session.get(Fqc.class, 1);
        assertNotNull(fqc);
        FqcSettingHistory history = new FqcSettingHistory(fqc, "A-Test");
        session.save(history);
        assertTrue(history.getId() != 0);
        System.out.println("Setting history's id: " + history.getId());
    }

//    @Test
    @Rollback(true)
    public void testRecordCheck() {
        List<FqcLoginRecord> l = session
                .createCriteria(FqcLoginRecord.class).list();

        FqcLine line = session.get(FqcLine.class, 1);

        FqcLoginRecord pojo = new FqcLoginRecord(line, "A-8888");

        FqcLoginRecord existRecord = l.stream()
                .filter(p -> Objects.equals(p.getJobnumber(), pojo.getJobnumber())
                || Objects.equals(p.getFqcLine(), pojo.getFqcLine()))
                .findFirst().orElse(null);

        assertNotNull(existRecord);

        HibernateObjectPrinter.print(existRecord);

        checkArgument(existRecord == null, "Jobnumber or FqcLine is already in fqcRecord.");
    }

//    @Test
//    @Rollback(false)
    public void testModelSopRemark() {
        ModelSopRemark remark = session.get(ModelSopRemark.class, 1);
        assertNotNull(remark);

        Line line = session.get(Line.class, 1);
        assertNotNull(line);

        remark.setLines(newHashSet(line));
        session.save(remark);
    }

//    @Test
    @Rollback(false)
    public void testFqcModelStandardTime() {
        FqcModelStandardTime standardTime = new FqcModelStandardTime();
        standardTime.setModelNameCategory("BO");
        standardTime.setStandardTime(132);
        session.save(standardTime);
        assertTrue(standardTime.getId() != 0);
    }

//    @Test
    @Rollback(true)
    public void testModelSopRemarkRelation() {
        //Find detail which detail group equals 2 and modelName equals :modelName

        List<ModelSopRemarkDetail> l = session.createSQLQuery("{CALL usp_GetModelSopRemarkDetail(:modelName, :people)}")
                .addEntity("d", ModelSopRemarkDetail.class)
                //.addJoin("r", "d.modelSopRemark")
                .setParameter("modelName", "UTC-520D-RE")
                .setParameter("people", 2)
                .list();

        ModelSopRemark m = l.get(0).getModelSopRemark();
        Hibernate.initialize(m);

        HibernateObjectPrinter.print(l);
    }

//    @Test
    @Rollback(false)
    public void testBabStandardTimeHistory() {
        Bab b = session.get(Bab.class, 173);
        assertNotNull(b);

        session.save(new BabStandardTimeHistory(b, new BigDecimal(291.3)));

    }

//    @Test
    @Rollback(true)
    public void testBabLineProductivityExcludeModel() {
        User user = session.get(User.class, 1);
        assertNotNull(user);
        BabLineProductivityExcludeModel exM = new BabLineProductivityExcludeModel("testModel", user);

        session.save(exM);

        HibernateObjectPrinter.print(exM);
    }

//    @Test
    @Rollback(true)
    public void testCountermeasureType() {
        Countermeasure cm = session.get(Countermeasure.class, 1);
        assertNotNull(cm);
        CountermeasureType cmT = cm.getCountermeasureType();
        assertNotNull(cmT);
        assertTrue(cmT.getId() == 1);
        HibernateObjectPrinter.print(cmT);
    }

//    @Test
    @Rollback(false)
    public void testBabLineProductivityExclude() {
//        26600
        Bab b = session.get(Bab.class, 26600);
        assertNotNull(b);
        BabLineProductivityExclude e = new BabLineProductivityExclude();
        e.setBab(b);
        session.save(e);
    }

//    @Test
    @Rollback(true)
    public void testQueryBarcode() {
        Line line = session.get(Line.class, 2);

        List l = session.createCriteria(BabPassStationRecord.class, "bpr")
                .createAlias("bpr.bab", "b")
                .add(Restrictions.eq("b.line", line))
                .list();
        assertEquals(44950, l.size());

    }

//    @Test
    @Rollback(true)
    public void testGetLastInputBarcode() {
        BabPassStationRecord b = (BabPassStationRecord) session.createCriteria(BabPassStationRecord.class)
                .createAlias("bab", "b")
                .createAlias("b.babSettingHistorys", "h")
                .createAlias("h.tagName", "t")
                .add(Restrictions.eq("t.name", "NA-S-1"))
                .add(Restrictions.isNull("h.lastUpdateTime"))
                .setMaxResults(1)
                .uniqueResult();

        HibernateObjectPrinter.print(b);

    }

//    @Test
    @Rollback(true)
    public void testPreAssyModuleStandardTime() {
        PreAssyModuleStandardTime b = (PreAssyModuleStandardTime) session.createCriteria(PreAssyModuleStandardTime.class)
                .add(Restrictions.idEq(1))
                .uniqueResult();

        HibernateObjectPrinter.print(b);

    }

    @Test
    @Rollback(true)
    public void testPreAssyModuleType() {
        PreAssyModuleType t = (PreAssyModuleType) session.createCriteria(PreAssyModuleType.class)
                .add(Restrictions.idEq(161))
                .uniqueResult();

        HibernateObjectPrinter.print(t);

    }

//    @Test
    @Rollback(true)
    public void testPreAssyModuleType2() {
        List<PreAssyModuleType> l = session.createCriteria(PreAssyModuleType.class).list();
        Bab b = session.get(Bab.class, 173);
        assertNotNull(b);
        assertTrue(!l.isEmpty());
        Set s = new HashSet(l);
        b.setPreAssyModuleTypes(s);

        session.save(b);

    }

//    @Test
    @Rollback(true)
    public void testPreAssyModuleStandardTime2() {
        Bab b = session.get(Bab.class, 38254);
        assertNotNull(b);

        List<PreAssyModuleStandardTime> l = session.createCriteria(PreAssyModuleStandardTime.class)
                .add(Restrictions.eq("modelName", b.getModelName()))
                .add(Restrictions.in("preAssyModuleType", b.getPreAssyModuleTypes()))
                .list();

        HibernateObjectPrinter.print(l);

    }

    @Autowired
    private WebServiceRV rv;

    @Autowired
    private com.advantech.service.db1.TestService testService;

    @Autowired
    private TestPassStationDetailService testPassStationDetailService;

//    @Test
    @Rollback(false)
    public void testPassStationDetails() {
        List<TestPassStationDetail> result = new ArrayList();

        List<TestPassStationDetail> dbData = testPassStationDetailService.findAll();

        DateTime eD = new DateTime().withTime(8, 0, 0, 0);
        DateTime sD = eD.minusMonths(8).withTime(8, 0, 0, 0);
        List<com.advantech.model.db1.Test> users = testService.findAll();

        List<Integer> stations = newArrayList(3, 11, 30, 151);

        stations.forEach(s -> {
            Section section = (s == 3 ? Section.BAB : Section.TEST);
            List<TestPassStationDetail> l = rv.getTestPassStationDetails2(users, section, s, sD, eD, Factory.DEFAULT);
            result.addAll(l);
        });

        assertTrue(!result.isEmpty());

        List<TestPassStationDetail> delData = (List<TestPassStationDetail>) CollectionUtils.subtract(dbData, result);
        testPassStationDetailService.delete(delData);
        System.out.println("Delete data cnt " + delData.size());

        List<TestPassStationDetail> newData = (List<TestPassStationDetail>) CollectionUtils.subtract(result, dbData);
        testPassStationDetailService.insert(newData);
        System.out.println("New data cnt " + newData.size());

    }

//    @Test
    @Rollback(true)
    public void testPrepareSchedule() {
        PrepareSchedule p = session.get(PrepareSchedule.class, 30);

        assertNotNull(p);

        HibernateObjectPrinter.print(p);
    }

//    @Test
    @Rollback(false)
    public void testUserInsert() {
        User user = session.get(User.class, 1);

        assertNotNull(user);

        user.setUsernameCh("中文");

        session.update(user);
    }

//    @Test
    @Rollback(true)
    public void testWorktime() {
        Worktime worktime = session.get(Worktime.class, 1);

        assertNotNull(worktime);

        HibernateObjectPrinter.print(worktime);
    }

}
