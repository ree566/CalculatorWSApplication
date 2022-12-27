/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 儲存MES的PassCnt做異常資料比對
 */
package com.advantech.quartzJob;

import com.advantech.helper.ApplicationContextHelper;
import com.advantech.model.db1.MesLine;
import com.advantech.model.db1.MesPassCountRecord;
import com.advantech.service.db1.MesLineService;
import com.advantech.service.db1.MesPassCountService;
import com.advantech.webservice.Factory;
import com.advantech.webservice.WebServiceRV;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author Wei.Cheng
 */
public class InsertMesCountRecord extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(InsertMesCountRecord.class);

    private MesLineService mesLineService;
    private MesPassCountService mesPassCountService;
    private WebServiceRV rv;

    public InsertMesCountRecord() {
        mesLineService = (MesLineService) ApplicationContextHelper.getBean("mesLineService");
        mesPassCountService = (MesPassCountService) ApplicationContextHelper.getBean("mesPassCountService");
        rv = (WebServiceRV) ApplicationContextHelper.getBean("webServiceRV");
    }

    @Override
    public void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        DateTime eD = new DateTime();
        DateTime sD = eD.minusDays(30);

        List<MesLine> lines = mesLineService.findAll();
        List<MesPassCountRecord> existData = mesPassCountService.findAll();

        List<Integer> lineId = lines.stream().map(MesLine::getId).collect(toList());

        List<MesPassCountRecord> l = rv.getMesPassCountRecords(sD, eD, Factory.TWM3);

        List<MesPassCountRecord> newData = l.stream()
                .filter(e -> lineId.contains(e.getMesLineId()) && !existData.contains(e))
                .collect(toList());

        newData.forEach(e -> {
            e.setId(0);
        });

        if (!newData.isEmpty()) {
            log.info("Begin insert " + newData.size() + " mes record into database.");
            mesPassCountService.insert(newData);
        } else {
            log.info("New data is empty.");
        }
    }
}
