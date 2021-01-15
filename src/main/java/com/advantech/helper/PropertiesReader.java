/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import com.advantech.model.db1.BabDataCollectMode;
import com.advantech.model.db1.LineTypeConfig;
import com.advantech.service.db1.LineTypeConfigService;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Wei.Cheng
 */
@Component
public class PropertiesReader {

    private static final Logger log = LoggerFactory.getLogger(PropertiesReader.class);

    @Value("${test.maxTable: 42}")
    private Integer maxTestTables;

    @Value("${test.productivity.bypass.hours: 0}")
    private int[] testByPassHours;

    @Value("${bab.saveToRecord.quantity: 0}")
    private Integer babSaveMininumQuantity;

    @Value("${bab.lineBalance.roundingDigit: 4}")
    private Integer babLineBalanceRoundDigit;

    @Value("${bab.lineBalance.difference: 0.05}")
    private Double babLineBalanceAlarmDiff;

    @Value("${sensorDetect.expireTime: 30}")
    private Integer sensorDetectExprieTime;

    @Value("${sensorDetect.period: 30}")
    private Integer sensorDetectExpriePeriod;

    @Value("${result.write.to.database: true}")
    private Boolean isCalculateResultWriterToDatabase;

    @Value("${result.save.to.oldServer: false}")
    private Boolean isResultWriteToOldDatabase;

    @Value("${endpoint.quartz.trigger}")
    private String endpointPollingCron;

    @Value("${test.salt.productivity: 0}")
    private Double testSaltProductivity;

    @Value("${bab.salt.productivity: 0}")
    private Double babSaltProductivity;

    @Value("${bab.data.collect.mode: 1}")
    private BabDataCollectMode babDataCollectMode;

    //Settings inject from database
    private BigDecimal assyLineBalanceStandard = new BigDecimal(0.8);
    private BigDecimal packingLineBalanceStandard = new BigDecimal(0.8);
    private BigDecimal babAlarmPercentStandard = new BigDecimal(0.3);
    private BigDecimal packingAlarmPercentStandard = new BigDecimal(0.3);
    private BigDecimal testProductivityStandardMin = new BigDecimal(0.8);
    private BigDecimal testProductivityStandardMax = new BigDecimal(1.2);
    private BigDecimal cellProductivityStandardMin = new BigDecimal(0.8);
    private BigDecimal cellProductivityStandardMax = new BigDecimal(1.2);

    @Value("${bab.countermeasure.alert.day: 1}")
    private int babCountermeasureAlertDay;

    @Autowired
    private LineTypeConfigService lineTypeConfigService;

    @PostConstruct
    private void init() {
        log.info(PropertiesReader.class + " init properties");
        List<LineTypeConfig> configs = lineTypeConfigService.findWithLineType();
        List<LineTypeConfig> balanceStandard = configs.stream()
                .filter(o -> "LINEBALANCE_STANDARD".equals(o.getName())).collect(Collectors.toList());
        List<LineTypeConfig> alarmPercentStandard = configs.stream()
                .filter(o -> "ALARM_PERCENT_STANDARD".equals(o.getName())).collect(Collectors.toList());
        List<LineTypeConfig> pStandardMax = configs.stream()
                .filter(o -> "PRODUCTIVITY_STANDARD_MAX".equals(o.getName())).collect(Collectors.toList());
        List<LineTypeConfig> pStandardMin = configs.stream()
                .filter(o -> "PRODUCTIVITY_STANDARD_MIN".equals(o.getName())).collect(Collectors.toList());

        balanceStandard.forEach(o -> {
            String st = o.getLineType().getName();
            switch (st) {
                case "ASSY":
                    assyLineBalanceStandard = o.getValue();
                    break;
                case "Packing":
                    packingAlarmPercentStandard = o.getValue();
                    break;
                default:
                    break;
            }
        });

        alarmPercentStandard.forEach(o -> {
            String st = o.getLineType().getName();
            switch (st) {
                case "ASSY":
                    babAlarmPercentStandard = o.getValue();
                    break;
                case "Packing":
                    packingLineBalanceStandard = o.getValue();
                    break;
                default:
                    break;
            }
        });

        pStandardMax.forEach(o -> {
            String st = o.getLineType().getName();
            switch (st) {
                case "Cell":
                    cellProductivityStandardMax = o.getValue();
                    break;
                case "Test":
                    testProductivityStandardMax = o.getValue();
                    break;
                default:
                    break;
            }
        });

        pStandardMin.forEach(o -> {
            String st = o.getLineType().getName();
            switch (st) {
                case "Cell":
                    cellProductivityStandardMin = o.getValue();
                    break;
                case "Test":
                    testProductivityStandardMin = o.getValue();
                    break;
                default:
                    break;
            }
        });

    }

    public Integer getMaxTestTables() {
        return maxTestTables;
    }

    public int[] getTestByPassHours() {
        return testByPassHours;
    }

    public Integer getBabSaveMininumQuantity() {
        return babSaveMininumQuantity;
    }

    public Integer getBabLineBalanceRoundDigit() {
        return babLineBalanceRoundDigit;
    }

    public Double getBabLineBalanceAlarmDiff() {
        return babLineBalanceAlarmDiff;
    }

    public Integer getSensorDetectExprieTime() {
        return sensorDetectExprieTime;
    }

    public Integer getSensorDetectExpriePeriod() {
        return sensorDetectExpriePeriod;
    }

    public Boolean getIsCalculateResultWriterToDatabase() {
        return isCalculateResultWriterToDatabase;
    }

    public Boolean getIsResultWriteToOldDatabase() {
        return isResultWriteToOldDatabase;
    }

    public String getEndpointPollingCron() {
        return endpointPollingCron;
    }

    public BigDecimal getAssyLineBalanceStandard() {
        return assyLineBalanceStandard;
    }

    public BigDecimal getPackingLineBalanceStandard() {
        return packingLineBalanceStandard;
    }

    public BigDecimal getBabAlarmPercentStandard() {
        return babAlarmPercentStandard;
    }

    public BigDecimal getPackingAlarmPercentStandard() {
        return packingAlarmPercentStandard;
    }

    public BigDecimal getTestProductivityStandardMin() {
        return testProductivityStandardMin;
    }

    public BigDecimal getTestProductivityStandardMax() {
        return testProductivityStandardMax;
    }

    public BigDecimal getCellProductivityStandardMin() {
        return cellProductivityStandardMin;
    }

    public BigDecimal getCellProductivityStandardMax() {
        return cellProductivityStandardMax;
    }

    public LineTypeConfigService getLineTypeConfigService() {
        return lineTypeConfigService;
    }

    public Double getTestSaltProductivity() {
        return testSaltProductivity;
    }

    public void setTestSaltProductivity(Double testSaltProductivity) {
        this.testSaltProductivity = testSaltProductivity;
    }

    public Double getBabSaltProductivity() {
        return babSaltProductivity;
    }

    public BabDataCollectMode getBabDataCollectMode() {
        return babDataCollectMode;
    }

    public int getBabCountermeasureAlertDay() {
        return babCountermeasureAlertDay;
    }

}
