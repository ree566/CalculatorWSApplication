/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import com.advantech.dao.db1.BabDAO;
import com.advantech.dao.db1.BabPcsDetailHistoryDAO;
import com.advantech.dao.db1.SqlProcedureDAO;
import com.advantech.helper.HibernateObjectPrinter;
import com.advantech.model.db1.Bab;
import java.util.List;
import org.joda.time.DateTime;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSqlProcedure {

    @Autowired
    private SqlProcedureDAO procDAO;

    @Autowired
    private BabPcsDetailHistoryDAO babPcsDetailHistoryDAO;
    
    @Autowired
    private BabDAO babDAO;

//    @Test
    @Transactional
    @Rollback(true)
    public void testGetBabDetail() {
        DateTime sD = new DateTime(2017, 12, 01, 0, 0, 0, 0);
        DateTime eD = new DateTime(2017, 12, 31, 0, 0, 0, 0);
//        List l = procDAO.findBabDetail("ASSY", "5", sD, eD, false);
//        assertNotEquals(0, l.size());
//        System.out.println(l.size());
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testBabPcsDetailHistory() {
        List l = babPcsDetailHistoryDAO.findByBabForMap(14223);
        HibernateObjectPrinter.print(l);
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testProc() {
        DateTime sD = new DateTime("2020-02-03");
        DateTime eD = new DateTime("2020-02-07");
        String st = "";
        int i = 1;
        
        assertTrue(!procDAO.findBabBestLineBalanceRecord(1, sD, eD).isEmpty());

        assertTrue(!procDAO.findBabDetail(1, 1, sD, eD, true).isEmpty());
        
        procDAO.findBabDetailWithBarcode(1, 1, sD, eD, true);
        
        procDAO.findBabLastBarcodeStatus(123);
        procDAO.findBabLastGroupStatus(123);
        
        assertTrue(!procDAO.findBabLineProductivity(null, null, 1, 1, null, 1, sD, eD).isEmpty());
        procDAO.findBabLineProductivityWithBarcode(null, null, 1, null, 1, sD, eD);
        
        procDAO.findBabPassStationExceptionReport(null, null, sD, eD, 0); 

        procDAO.findBabPassStationRecord(null, null, sD, eD, "ASSY");

        assertTrue(!procDAO.findBabPcsDetail(null, "ASSY", sD, eD).isEmpty());
        procDAO.findBabPcsDetailWithBarcode(st, st, sD, eD);

        assertTrue(!procDAO.findBabPreAssyProductivity(1, 1, sD, eD).isEmpty());
        
        procDAO.findLineBalanceCompare(st, "ASSY");
        procDAO.findLineBalanceCompareByBab(i);
        procDAO.findLineBalanceCompareByBabWithBarcode(i);
        assertTrue(!procDAO.findPreAssyModuleUnexecuted(sD, eD).isEmpty());
        procDAO.findSensorCurrentGroupStatus(i);
        assertTrue(!procDAO.findTestPassStationProductivity(sD, eD).isEmpty());
        
//        procDAO.getTotalAbnormalData(i); //proc M3_BW.sensorTotalAbnormalCheck not found
//        procDAO.getAbnormalData(i); proc not found
        
        Bab b = babDAO.findByPrimaryKey(185);
//        procDAO.closeBabDirectly(b);
//        procDAO.closeBabWithSaving(b);
//        procDAO.closeBabWithSavingWithBarcode(b);
        
//        procDAO.sensorDataClean(sD.withTime(0, 0, 0, 0).toDate());
        
//        procDAO.findWorktime();
//        procDAO.findWorktime(st);
//        procDAO.findUserInfoRemote();
//        procDAO.findUserInfoRemote(st);
    }

}
