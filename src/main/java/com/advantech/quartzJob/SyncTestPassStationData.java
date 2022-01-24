/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.model.db1.TestPassStationDetail;
import com.advantech.model.db1.TestRecord;
import com.advantech.service.db1.TestPassStationDetailService;
import com.advantech.service.db1.TestRecordService;
import com.advantech.webservice.Factory;
import com.advantech.webservice.mes.Section;
import com.advantech.webservice.WebServiceRV;
import static com.google.common.collect.Lists.newArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng Sync back excel's data from "MFG-Server (MFG-OAPC-019B)"
 * Every day ※Only sync current years data
 */
@Component
public class SyncTestPassStationData {
    
    private static final Logger logger = LoggerFactory.getLogger(SyncTestPassStationData.class);
    
    @Autowired
    private WebServiceRV rv;
    
    @Autowired
    private TestRecordService testRecordService;
    
    @Autowired
    private TestPassStationDetailService testPassStationDetailService;
    
    private final List<Integer> stations = newArrayList(3, 11, 30, 151);
    
    @Transactional
    public void execute() {
        List<TestPassStationDetail> result = new ArrayList();
        
        DateTime today = new DateTime();
        
        DateTime sD = new DateTime(today).minusDays(today.getDayOfWeek() == 1 ? 3 : 1).withTime(8, 0, 0, 0);
        DateTime eD = new DateTime(today).withTime(8, 0, 0, 0);
        
        List<TestPassStationDetail> dbData = testPassStationDetailService.findByDate(sD, eD);
        List<TestRecord> records = testRecordService.findByDate(sD, eD, false);
        List<String> jobnumbers = records.stream().map(t -> "'" + t.getUserId() + "'").distinct().collect(Collectors.toList());
        
        stations.forEach(s -> {
            Section section = (s == 3 ? Section.BAB : Section.TEST);
            List<TestPassStationDetail> l = rv.getTestPassStationDetails(jobnumbers, section, s, sD, eD, Factory.DEFAULT);
            List<TestPassStationDetail> l2 = rv.getTestPassStationDetails(jobnumbers, section, s, sD, eD, Factory.TEMP1);
            result.addAll(l);
            result.addAll(l2);
        });
        
        if (!result.isEmpty()) {
            List<TestPassStationDetail> delData = (List<TestPassStationDetail>) CollectionUtils.subtract(dbData, result);
            testPassStationDetailService.delete(delData);
            logger.info("Delete data cnt " + delData.size());
            
            List<TestPassStationDetail> newData = (List<TestPassStationDetail>) CollectionUtils.subtract(result, dbData);
            testPassStationDetailService.insert(newData);
            logger.info("New data cnt " + newData.size());
        }
    }
    
}
