/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import com.advantech.dao.db1.BabDAO;
import com.advantech.dao.db1.BabPcsDetailHistoryDAO;
import com.advantech.dao.db1.BabPreAssyPcsRecordDAO;
import com.advantech.dao.db1.BabSettingHistoryDAO;
import com.advantech.dao.db1.CountermeasureDAO;
import com.advantech.dao.db1.FqcDAO;
import com.advantech.dao.db1.PrepareScheduleDailyRemarkDAO;
import com.advantech.dao.db1.SensorTransformDAO;
import com.advantech.dao.db1.SqlViewDAO;
import com.advantech.dao.db1.TagNameComparisonDAO;
import com.advantech.helper.DateTimeGapFinder;
import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabPreAssyPcsRecord;
import com.advantech.model.db1.BabSettingHistory;
import com.advantech.model.db1.Countermeasure;
import com.advantech.model.db1.Line;
import com.advantech.model.db1.MesLine;
import com.advantech.model.db1.MesPassCountRecord;
import com.advantech.model.db1.SensorTransform;
import com.advantech.model.view.db1.BabProcessDetail;
import com.advantech.model.view.db5.LackingInfo;
import com.advantech.webservice.Factory;
import com.advantech.webservice.WebServiceRV;
import com.fasterxml.jackson.core.JsonProcessingException;
import static com.google.common.collect.Lists.newArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    private TagNameComparisonDAO tagDAO;

    @Autowired
    private CountermeasureDAO countermeasureDAO;

    @Autowired
    private FqcDAO fqcDAO;

    @Autowired
    private SqlViewDAO sqlViewDAO;

    @Autowired
    private BabPreAssyPcsRecordDAO prePcsRecDAO;

    @Autowired
    private WebServiceRV rv;
    
    @Autowired
    private PrepareScheduleDailyRemarkDAO preRemarkDAO;

//    @Test
    @Transactional
    @Rollback(true)
    public void testBabDAO() throws JsonProcessingException {

//        for (int i = 1; i <= 4; i++) {
//            List l = babDAO.findUnReplyed(i);
//            assertTrue(l.isEmpty());
//        }
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

//    @Test
    @Transactional
    @Rollback(true)
    public void testBabPcsDetailHistoryDAO() throws JsonProcessingException, ParseException {
        List<Map> l = babPcsDetailHistoryDAO.findByBabForMap(12597);
        assertNotEquals(0, l.size());
        Map m = l.get(0);
        assertNotNull(m.get("id"));
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

//    @Test
    @Transactional
    @Rollback(true)
    public void testTagNameCompRange() {
        SensorTransform sensor = sensorTransformDAO.findByPrimaryKey("L4-S-1");
        HibernateObjectPrinter.print(this.tagDAO.findInRange(sensor, 5));
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testCountermeasure() {
        Countermeasure cm = countermeasureDAO.findByBabAndTypeName(23365, "Bab_Abnormal_LineProductivity");
        assertNotNull(cm);
        assertNotNull(cm.getCountermeasureType());
        assertEquals(2, cm.getCountermeasureType().getId());
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testFqc() {
        List l = fqcDAO.findProcessingWithLine();
        assertTrue(l.isEmpty());
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testSqlView() {
        DateTime eD = new DateTime();
        DateTime sD = eD.minusWeeks(1);
//        List l = sqlViewDAO.findBabPassStationExceptionReport(null, null, sD, eD, 1);
//        assertTrue(l.isEmpty());
//        HibernateObjectPrinter.print(l);
    }

//    @Test
    @Transactional
    @Rollback(false)
    public void testBabPreAssyPcsRecord() {
        BabSettingHistory history = babSettingHistoryDAO.findByPrimaryKey(1);
        assertNotNull(history);
        BabPreAssyPcsRecord pcsRec = new BabPreAssyPcsRecord(history, 50);
        prePcsRecDAO.insert(pcsRec);
    }

//    @Test
    @Transactional
    @Rollback(false)
    public void testMesCountRecord() {
        DateTime eD = new DateTime();
        DateTime sD = eD.minusDays(30);

        Session session = sessionFactory.getCurrentSession();

        List<MesLine> lines = session.createCriteria(MesLine.class).list();
        List<MesPassCountRecord> existData = session.createCriteria(MesPassCountRecord.class).list();

        List<Integer> lineId = lines.stream().map(MesLine::getId).collect(toList());
        assertTrue(!lineId.isEmpty());

        List<MesPassCountRecord> l = rv.getMesPassCountRecords(sD, eD, Factory.DEFAULT);

        System.out.println(l.size());

        List<MesPassCountRecord> newData = l.stream()
                .filter(e -> lineId.contains(e.getMesLineId()) && !existData.contains(e))
                .collect(toList());

        System.out.println(newData.size());

        assertTrue(!newData.isEmpty());

        newData.forEach(e -> {
            e.setId(0);
            session.merge(e);
        });
    }

    private boolean isRecordExist(List<MesPassCountRecord> existData, MesPassCountRecord newData) {
        List<MesPassCountRecord> l = existData.stream()
                .filter(e -> e.equals(newData))
                .collect(toList());
        return !l.isEmpty();
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testFindBabLastInputPerLine() {
        List<Bab> l = this.sqlViewDAO.findBabLastInputPerLine();
        assertEquals(9, l.size());
        HibernateObjectPrinter.print(l);
    }

    @Autowired
    private DateTimeGapFinder finder;

//    @Test
    @Transactional
    @Rollback(true)
    public void testFindDateGap() {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateOnly_fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime sD = fmt.parseDateTime("2018-08-20 08:30:00");
        DateTime eD = fmt.parseDateTime("2018-08-24 17:30:00");
        List<Bab> l = babDAO.findByDate(sD, eD);
        assertTrue(!l.isEmpty());

        Map<Integer, Map<String, List<Bab>>> result = l.stream()
                .collect(Collectors.groupingBy(b -> b.getLine().getId(),
                        Collectors.groupingBy(b -> dateOnly_fmt.print(new DateTime(b.getBeginTime()))))
                );

        List<BabProcessDetail> gapDetailsSum = new ArrayList();

        result.forEach((k, v) -> {
            v.forEach((k1, v1) -> {
                DateTime sDOD = new DateTime(k1).withTime(8, 30, 0, 0);
                DateTime eDOD = new DateTime(k1).withTime(17, 30, 0, 0);
                List<Interval> gaps = this.searchGaps(v1, sDOD, eDOD);
                int totalMinutes = 0;
                totalMinutes = gaps.stream().map((i) -> Minutes.minutesBetween(i.getStart(), i.getEnd()).getMinutes()).reduce(totalMinutes, Integer::sum);
                BabProcessDetail detail = new BabProcessDetail();
                detail.setLineId(k);
                detail.setDateString(k1);
                detail.setBabs(v1);
                detail.setIntervals(gaps);
                detail.setTotalGapsTimeInDay(totalMinutes);
                gapDetailsSum.add(detail);
            });
        });

        gapDetailsSum.forEach(b -> {
            System.out.printf("Date: %s, line_id: %d, times: %d\r\n",
                    b.getDateString(), b.getLineId(), b.getTotalGapsTimeInDay());
        });
    }

    private List<Interval> searchGaps(List<Bab> l, DateTime startTimeOfDay, DateTime endTimeOfDay) {

        //Turn startDate & endDate into Interval object
        List<Interval> existingIntervals = new ArrayList();
        l.forEach(b -> {
            existingIntervals.add(new Interval(new DateTime(b.getBeginTime()), new DateTime(b.getLastUpdateTime())));
        });

        List<Interval> mergedIntervals = finder.mergeIntervals(existingIntervals);

//        System.out.println("The Merged Intervals are: ");
//        mergedIntervals.forEach(i -> {
//            System.out.println("[" + i.getStart() + " --- " + i.getEnd() + "] ");
//        });
        Interval workTimeInDay = new Interval(startTimeOfDay, endTimeOfDay);
        List<Interval> bigSearchResults = finder.findGaps(mergedIntervals, workTimeInDay);

        return bigSearchResults;
//        bigSearchResults.forEach(i -> {
//            System.out.printf("Start: %s --- End: %s \r\n", fmt.print(i.getStart()), fmt.print(i.getEnd()));
//        });
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testFindBabUnreply() {
        List<Bab> l = this.babDAO.findUnReplyed(1, new DateTime().minusDays(1), new DateTime());
        assertTrue(!l.isEmpty());
        HibernateObjectPrinter.print(l);
    }

    @Autowired
    @Qualifier("sqlViewDAO4")
    private com.advantech.dao.db4.SqlViewDAO sqlViewDAO4;

//    @Test
    @Transactional("tx4")
    @Rollback(true)
    public void testFindMesPassStationInfo() {
        List<String> po = newArrayList(
                "PCJ9119ZA",
                "PSJA042ZA",
                "PIJA158ZA",
                "PAJ9011ZA",
                "THJ000235ZA",
                "THJ000420ZA",
                "THJ000232ZA",
                "THJ000233ZA",
                "PSJA201ZA",
                "THJ000064ZA"
        );
        List list = sqlViewDAO4.findMesPassStationInfo(po, new DateTime());
        list.forEach(o -> {
            HibernateObjectPrinter.print(o);
        });
    }

    @Autowired
    @Qualifier("sqlViewDAO3")
    private com.advantech.dao.db3.SqlViewDAO sqlViewDAO3;

//    @Test
    @Transactional("tx3")
    @Rollback(true)
    public void testFindWorktimeFromRemote() {
        List list = sqlViewDAO3.findWorktime();

        HibernateObjectPrinter.print(list.get(0));

    }
    
//    @Test
    @Transactional
    @Rollback(true)
    public void testPrepareScheduleDailyRemark() {
        List list = preRemarkDAO.findAll();

        HibernateObjectPrinter.print(list.get(0));

    }
    
    @Autowired
    @Qualifier("sqlViewDAO5")
    private com.advantech.dao.db5.SqlViewDAO sqlViewDAO5;
    
    @Test
    @Transactional
    @Rollback(true)
    public void testFindLackingInfo() {
        List<LackingInfo> list = sqlViewDAO5.findLackingInfo();

        HibernateObjectPrinter.print(list);
        
//        HibernateObjectPrinter.print(list.get(0).getCnt().getClass().getName());

    }
    
    @Autowired
    @Qualifier("sqlViewDAO6")
    private com.advantech.dao.db6.SqlViewDAO sqlViewDAO6;
    
    @Test
    @Transactional
    @Rollback(true)
    public void testFindShortageInfo() {
        List list = sqlViewDAO6.findShortageInfo();

        HibernateObjectPrinter.print(list);
        
//        HibernateObjectPrinter.print(list.get(0).getCnt().getClass().getName());

    }

}
