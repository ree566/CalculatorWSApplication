/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 把燈號顯示兩個主要的txt裏頭的參數由1(開燈)轉0(關燈)用
 */
package com.advantech.quartzJob;

import com.advantech.helper.ApplicationContextHelper;
import com.advantech.facade.BabLineTypeFacade;
import com.advantech.service.db1.BabSensorLoginRecordService;
import com.advantech.service.db1.BabSettingHistoryService;
import com.advantech.facade.TestLineTypeFacade;
import com.advantech.service.db1.TestService;
import com.advantech.webservice.WaGetTagValue;
import java.io.IOException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author Wei.Cheng
 */
public class DataBaseInit extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(DataBaseInit.class);

    private final BabSettingHistoryService babSettingHistoryService;
    private final BabSensorLoginRecordService babSensorLoginRecordService;
    private final TestService testService;

    private final BabLineTypeFacade bF;
    private final TestLineTypeFacade tF;

    private final WaGetTagValue waGetTagValue;

    public DataBaseInit() {
        babSettingHistoryService = (BabSettingHistoryService) ApplicationContextHelper.getBean("babSettingHistoryService");
        babSensorLoginRecordService = (BabSensorLoginRecordService) ApplicationContextHelper.getBean("babSensorLoginRecordService");
        testService = (TestService) ApplicationContextHelper.getBean("testService");
        tF = (TestLineTypeFacade) ApplicationContextHelper.getBean("testLineTypeFacade");
        bF = (BabLineTypeFacade) ApplicationContextHelper.getBean("babLineTypeFacade");
        waGetTagValue = (WaGetTagValue) ApplicationContextHelper.getBean("waGetTagValue");
    }

    @Override
    public void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        dataInitialize();
    }

    private void dataInitialize() {
        try {
            babSettingHistoryService.init();
            babSensorLoginRecordService.init();
            testService.cleanTests();
            bF.resetAlarm();
            tF.resetAlarm();
            waGetTagValue.initActiveTagNodes();
            log.info("Data has been initialized.");
        } catch (IOException ex) {
            log.error("Data initialized fail because: " + ex);
        }

    }
}
