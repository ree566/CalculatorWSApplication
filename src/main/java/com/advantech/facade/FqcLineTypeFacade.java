/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.facade;

import com.advantech.model.db1.BabStatus;
import com.advantech.model.db1.Fqc;
import com.advantech.model.db1.FqcLine;
import com.advantech.model.db1.FqcModelStandardTime;
import com.advantech.model.db1.FqcSettingHistory;
import com.advantech.model.db1.FqcTimeTemp;
import com.advantech.model.db1.LineTypeConfig;
import com.advantech.model.db1.PassStationRecord;
import com.advantech.service.db1.FqcLineService;
import com.advantech.service.db1.FqcModelStandardTimeService;
import com.advantech.service.db1.FqcService;
import com.advantech.service.db1.FqcSettingHistoryService;
import com.advantech.service.db1.FqcTimeTempService;
import com.advantech.service.db1.LineTypeConfigService;
import com.advantech.webservice.WebServiceRV;
import static com.google.common.collect.Maps.newHashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Wei.Cheng
 */
@Component
public class FqcLineTypeFacade extends BasicLineTypeFacade {

    private static final Logger log = LoggerFactory.getLogger(FqcLineTypeFacade.class);

    @Autowired
    private FqcService fqcService;

    @Autowired
    private FqcLineService fqcLineService;

    @Autowired
    private FqcModelStandardTimeService standardTimeService;

    @Autowired
    private FqcTimeTempService fqcTimeTempService;

    @Autowired
    private FqcSettingHistoryService settingHistoryService;

    @Autowired
    private LineTypeConfigService lineTypeConfigService;

    @Autowired
    private WebServiceRV rv;

    private Double productivityMin, productivityMax;

    @PostConstruct
    protected void init() {
//        this.initMap();
        List<LineTypeConfig> l = lineTypeConfigService.findByLineType(5);
        LineTypeConfig conf = l.stream().filter(c -> "PRODUCTIVITY_STANDARD_MIN".equals(c.getName())).findFirst().orElse(null);
        productivityMin = conf == null ? 0.0 : conf.getValue().doubleValue();
        conf = l.stream().filter(c -> "PRODUCTIVITY_STANDARD_MAX".equals(c.getName())).findFirst().orElse(null);
        productivityMax = conf == null ? 0.0 : conf.getValue().doubleValue();
    }

    @Override
    protected void initMap() {
        this.dataMap.clear();
        List<FqcLine> l = fqcLineService.findAll();
        l.forEach((line) -> {
            dataMap.put(line.getName(), super.NORMAL_SIGN);
        });
    }

    /*
        Find all processing fqc with user info
        Get webService FQC_RI count and calc user's productivity
     */
    @Override
    public boolean generateData() {
        processingJsonObject = new JSONObject();
        boolean isSomeoneUnderStandard = false;

        List<Fqc> l = fqcService.findProcessingWithLine();
        List<Map<String, Object>> result = new ArrayList();

        if (l.isEmpty()) {
            return false;
        }

        List<FqcModelStandardTime> standardTimes = standardTimeService.findAll();
        Date now = new Date();

        Integer[] ids = l.stream().map(Fqc::getId).toArray(Integer[]::new);
        List<FqcTimeTemp> pauseTimeTemps = fqcTimeTempService.findByFqcIdIn(ids);
        List<FqcSettingHistory> historys = settingHistoryService.findByFqcIdIn(ids);

        //找進行的FQC, 抓標工, 抓MES台數, 抓進行時間(btime-now), 算效率
        for (Fqc fqc : l) {
            FqcLine line = fqc.getFqcLine();

            FqcTimeTemp tempLastRecord = pauseTimeTemps.stream()
                    .filter(o -> Objects.equals(o.getFqc().getId(), fqc.getId()))
                    .reduce((first, second) -> second)
                    .orElse(null);

            FqcSettingHistory history = historys.stream()
                    .filter(o -> Objects.equals(o.getFqc().getId(), fqc.getId()))
                    .findFirst()
                    .orElse(null);

            if (history == null) {
                continue;
            }

            FqcModelStandardTime standardTime = standardTimes.stream()
                    .filter(f -> fqc.getModelName().contains(f.getModelNameCategory()))
                    .max(Comparator.comparing(s -> s.getModelNameCategory().length()))
                    .orElse(null);

            List<PassStationRecord> records = rv.getPassStationRecords(fqc.getPo(), 16, line.getFactory());
            records.removeIf(rec -> !history.getJobnumber().equals(rec.getUserNo())
                    || rec.getCreateDate().before(fqc.getBeginTime())
                    || rec.getCreateDate().after(now));

            long seconds = 0L;

            if (tempLastRecord != null) {
                if (Objects.equals(BabStatus.PAUSE, fqc.getBabStatus()) && tempLastRecord.getLastUpdateTime() == null) {
                    seconds = tempLastRecord.getTimePeriod();
                } else if (fqc.getBabStatus() == null && tempLastRecord.getLastUpdateTime() != null) {
                    seconds = tempLastRecord.getTimePeriod() + ((now.getTime() - tempLastRecord.getLastUpdateTime().getTime()) / 1000);
                } else {
                    throw new IllegalStateException();
                }
            } else {
                seconds = (now.getTime() - fqc.getBeginTime().getTime()) / 1000;
            }

            Map<String, Object> info = newHashMap();
            info.put("fqcId", fqc.getId());
            info.put("modelName", fqc.getModelName());
            info.put("fqcLineName", line.getName());
            info.put("jobnumber", history.getJobnumber());
            info.put("standardTime", standardTime == null ? 0 : standardTime.getStandardTime());
            info.put("records", records.size());
            info.put("seconds", seconds);

            Double productivity = (standardTime == null || (int) seconds == 0 || records.isEmpty()) ? 0
                    : calculateProductivity(standardTime.getStandardTime(), (int) seconds, records.size());
            boolean isPass = productivity <= productivityMax || productivity >= productivityMin;
            info.put("productivity", productivity);
            info.put("isPass", isPass ? 1 : 0);
//            
            result.add(info);

            isSomeoneUnderStandard = true;
        }
        processingJsonObject.put("data", result);

        return false;

    }

    /*
            檢驗效率 = 標準工時 * N台 / (結束 - 開始) * N台
            N台 = 幾個FQC RI
     */
    private Double calculateProductivity(int standardTime, int firstPcsTimeCost, int currentPcs) {
        Double producitvity = ((double) standardTime * (double) currentPcs) / ((double) firstPcsTimeCost * (double) currentPcs);
        return producitvity;
    }

    @Override
    public void initAlarmSign() {
//        List l = almService.findAll();
//        almService.delete(l);
//        almService.insert(this.mapToAlarmSign(dataMap));
    }

    @Override
    public void setAlarmSign(List l) {
//        almService.update(l);
    }

    @Override
    public void resetAlarmSign() {
//        almService.reset();
    }

    @Override
    public void setAlarmSignToTestingMode() {
//        almService.AlarmToTestingMode();
    }

    @Override
    protected List mapToAlarmSign(Map map) {
        return null;
    }

}
