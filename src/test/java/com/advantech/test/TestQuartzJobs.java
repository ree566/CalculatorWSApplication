/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.quartzJob.CleanSensorData;
import com.advantech.quartzJob.CountermeasureAlarm;
import com.advantech.quartzJob.HandleUncloseBab;
import com.advantech.quartzJob.DataBaseInit;
import com.advantech.quartzJob.InsertMesCountRecord;
import com.advantech.quartzJob.PollingDataCollectStatus;
import com.advantech.quartzJob.SyncLineUserReference;
import com.advantech.quartzJob.SyncPrepareScheduleForAssy;
import com.advantech.quartzJob.SyncTestPassStationData;
import com.advantech.quartzJob.SyncUserFromRemote;
import com.advantech.quartzJob.TestLineTypeRecord;
import com.advantech.quartzJob.TestLineTypeRecordUnrepliedAlarm;
import com.advantech.quartzJob.ArrangePrepareScheduleImpl_Assy;
import com.advantech.quartzJob.ArrangePrepareScheduleImpl_Packing;
import com.advantech.quartzJob.SyncPrepareScheduleForPacking;
import com.advantech.quartzJob.SyncWorktimeFromRemote;
import static com.google.common.collect.Lists.newArrayList;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
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
public class TestQuartzJobs {

    @Autowired
    private PollingDataCollectStatus job;

    @Autowired
    private SyncTestPassStationData job2;

//    @Test
    public void testTestLineTypeRecord() throws JobExecutionException {
        TestLineTypeRecord tr = new TestLineTypeRecord();
        tr.executeInternal(null);
    }

//    @Test
    public void testDbInit() throws JobExecutionException {
        DataBaseInit d = new DataBaseInit();
        d.executeInternal(null);
    }

//    @Test
    public void testBabDataSaver() throws JobExecutionException {
        HandleUncloseBab b = new HandleUncloseBab();
        b.executeInternal(null);
    }

//    @Test
    public void testCleanSensorData() throws JobExecutionException {
        CleanSensorData c = new CleanSensorData();
        c.executeInternal(null);
    }

//    @Test
    public void testCountermeasureAlarm() throws Exception {
        CountermeasureAlarm c = new CountermeasureAlarm();
        c.execute();
    }

//    @Test
    public void testRecordUnrepliedAlarm() throws JobExecutionException {
        TestLineTypeRecordUnrepliedAlarm t = new TestLineTypeRecordUnrepliedAlarm();
        System.out.println(t.generateMailBody());
    }

//    @Test
    public void testPollingData() throws JobExecutionException {
        HibernateObjectPrinter.print(job.getData());
    }

//    @Test
    public void testInsertMesCountRecord() throws JobExecutionException {
        InsertMesCountRecord i = new InsertMesCountRecord();
        i.executeInternal(null);
    }

    @Test
    public void testSyncTestPassStationData() throws JobExecutionException {
        job2.execute();
    }

    @Autowired
    private SyncPrepareScheduleForAssy sps;

    @Autowired
    private ArrangePrepareScheduleImpl_Assy aps;

//    @Test
    public void testSyncPrepareSchedule1() throws Exception {
        //先設定好當日出勤名單, 才會給予字動排站
        //This is assy schedule

        DateTime d = new DateTime("2021-03-29");

//        sps.execute(d);
        aps.execute(newArrayList(d));
        //aps.execute();
    }

    @Autowired
    private SyncLineUserReference sur;

//    @Test
    public void testSyncLineUserReference() throws Exception {

        //DateTime d3 = new DateTime("2020-06-01");
        sur.execute();
    }

    @Autowired
    private SyncUserFromRemote suf;

//    @Test
    public void testSyncUserFromRemote() throws Exception {

        suf.execute();
    }

    @Autowired
    private SyncPrepareScheduleForPacking sps2;

    @Autowired
    private ArrangePrepareScheduleImpl_Packing apspkg;

//    @Test
    public void testSyncPrepareSchedule2() throws Exception {
        //先設定好當日出勤名單, 才會給予字動排站
        //This is packing schedule
//        DateTime d = new DateTime("2020-07-20");
//        sps2.execute(d);
        apspkg.execute();
    }

    @Autowired
    private SyncWorktimeFromRemote swr;

    @Test
    public void testSyncWorktimeFromRemote() throws Exception {
        swr.execute();
    }

}
