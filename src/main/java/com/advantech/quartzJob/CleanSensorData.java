/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.helper.ApplicationContextHelper;
import com.advantech.helper.DatetimeGenerator;
import com.advantech.service.db1.SqlProcedureService;
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
//Clean the data in FBN table before target date.
public class CleanSensorData extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(CleanSensorData.class);
    private final DatetimeGenerator dg = new DatetimeGenerator("yyyy-MM-dd");
    private static final int SPECIFY_DAY = 7;
    
    private final SqlProcedureService procService;

    public CleanSensorData() {
        procService = (SqlProcedureService) ApplicationContextHelper.getBean("sqlProcedureService");
    }

    @Override
    public void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        DateTime d = DateTime.now().minusDays(SPECIFY_DAY).withTime(0, 0, 0, 0);
        log.info("Begin clean sensor data date before " + dg.dateFormatToString(d));
        procService.sensorDataClean(d.toDate());
    }
}
