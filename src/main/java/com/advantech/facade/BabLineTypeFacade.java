/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.facade;

import com.advantech.helper.PropertiesReader;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.Line;
import com.advantech.model.db1.AlarmBabAction;
import com.advantech.model.db1.BabDataCollectMode;
import com.advantech.model.db1.BabSettingHistory;
import com.advantech.model.view.db1.BabLastBarcodeStatus;
import com.advantech.model.view.db1.BabLastGroupStatus;
import com.advantech.service.db1.AlarmBabActionService;
import com.advantech.service.db1.BabService;
import com.advantech.service.db1.BabSettingHistoryService;
import com.advantech.service.db2.LineBalancingService;
import com.advantech.service.db1.LineService;
import com.advantech.service.db1.SqlProcedureService;
import com.advantech.service.db1.SqlViewService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.stream.Collectors.toList;
import javax.annotation.PostConstruct;
import org.json.JSONArray;
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
public class BabLineTypeFacade extends BasicLineTypeFacade {

    private static final Logger log = LoggerFactory.getLogger(BabLineTypeFacade.class);

    @Autowired
    private LineBalancingService lineBalanceService;

    @Autowired
    private BabService babService;

    @Autowired
    private AlarmBabActionService almService;

    @Autowired
    private LineService lineService;

    @Autowired
    private SqlProcedureService procService;

    @Autowired
    private BabSettingHistoryService babSettingHistoryService;

    @Autowired
    private PropertiesReader p;

    private Double ASSY_STANDARD;

    private Double PKG_STANDARD;

    private BabDataCollectMode collectMode;

    private boolean isSomeBabUnderStandard = false;

    @PostConstruct
    protected void init() {
        log.info(BabLineTypeFacade.class.getName() + " init inner setting and db object.");
        ASSY_STANDARD = p.getAssyLineBalanceStandard().doubleValue();
        PKG_STANDARD = p.getPackingLineBalanceStandard().doubleValue();
        collectMode = p.getBabDataCollectMode();
        this.initMap();
//        this.initAlarmSign();
    }

    @Override
    public void initMap() {
        dataMap.clear();
        List<Line> babLineStatus = lineService.findAll();
        babLineStatus.forEach((line) -> {
            String lineName = line.getName().trim();
            for (int i = 1, length = line.getPeople(); i <= length; i++) {
                dataMap.put(lineName + "-L-" + i, super.NORMAL_SIGN);
            }
        });
    }

    //組測亮燈邏輯type 2(目前使用)
    @Override
    public boolean generateData() {

        isSomeBabUnderStandard = false;

        List<Bab> processingBabs = babService.findProcessing();
        List<BabSettingHistory> allBabSettings = babSettingHistoryService.findProcessing();

        if (hasDataInCollection(allBabSettings) && hasDataInCollection(processingBabs)) {
            //把所有有在bab資料表的id集合起來，找到統計值之後依序寫入txt(各線別取當日最早輸入的工單id來亮燈)
            JSONArray transBabData;
            processingJsonObject = new JSONObject();

            switch (collectMode) {
                case AUTO:
                    transBabData = getBabLineBalanceResultWithSensor(processingBabs, allBabSettings);
                    break;
                case MANUAL:
                    transBabData = getBabLineBalanceResultWithBarcode(processingBabs, allBabSettings);
                    break;
                default:
                    transBabData = new JSONArray();
                    break;
            }

            processingJsonObject.put("data", transBabData);
        } else {
            processingJsonObject = null;//drop the data if no data in the database
        }
        if (isSomeBabUnderStandard) {
            babDataToMap(processingJsonObject);
        }
        return isSomeBabUnderStandard;
    }

    private JSONArray getBabLineBalanceResultWithBarcode(List<Bab> processingBabs, List<BabSettingHistory> allBabSettings) {
        JSONArray transBabData = new JSONArray();

        /*
                Because BabLastGroupStatus.class is a sql view object
                Get all data in one transaction to prevent sql deadlock.
         */
        List<BabLastBarcodeStatus> status = procService.findBabLastBarcodeStatus(processingBabs);

        processingBabs.forEach((bab) -> {
            List<BabSettingHistory> babSettings = allBabSettings.stream()
                    .filter(rec -> rec.getBab().getId() == bab.getId()).collect(toList());
            List<BabLastBarcodeStatus> matchesStatus = status.stream()
                    .filter(stat -> stat.getBab_id() == bab.getId()).collect(toList());
            if (!(babSettings.isEmpty())) {
                int currentGroupSum = matchesStatus.size();//看目前組別人數是否有到達bab裏頭設定的人數
                int peoples = bab.getPeople();
                if (currentGroupSum == 0 || currentGroupSum != peoples) {
                    /*
                    Insert an empty status
                    BabSettingHistory in allBabSettings are proxy object generate by hibernate
                    Can't transform to json by google.Gson directly
                     */
                    babSettings.forEach((setting) -> {
                        JSONObject obj = new JSONObject();
                        obj.put("tagName", setting.getTagName().getName());
                        obj.put("station", setting.getStation());
                        transBabData.put(obj);
                    });

                    isSomeBabUnderStandard = true;

                } else {
                    BabLastBarcodeStatus maxStatus = matchesStatus.stream()
                            .max((p1, p2) -> Double.compare(p1.getDiff(), p2.getDiff())).get();
                    double diffTimeSum = matchesStatus.stream().mapToDouble(BabLastBarcodeStatus::getDiff).sum();

                    boolean isUnderBalance = checkIsUnderBalance(bab, maxStatus.getDiff(), diffTimeSum);
//
                    if (isUnderBalance) {
                        isSomeBabUnderStandard = true;
                    }

                    matchesStatus.stream().map((bgs) -> {
                        bgs.setIsmax(isUnderBalance && Objects.equals(bgs, maxStatus));
                        return bgs;
                    }).forEachOrdered((bgs) -> {
                        transBabData.put(new JSONObject(bgs));
                    });
                }
            }
        });
        return transBabData;
    }

    private JSONArray getBabLineBalanceResultWithSensor(List<Bab> processingBabs, List<BabSettingHistory> allBabSettings) {
        JSONArray transBabData = new JSONArray();

        /*
                Because BabLastGroupStatus.class is a sql view object
                Get all data in one transaction to prevent sql deadlock.
         */
        List<BabLastGroupStatus> status = procService.findBabLastGroupStatus(processingBabs);

        processingBabs.forEach((bab) -> {
            List<BabSettingHistory> babSettings = allBabSettings.stream()
                    .filter(rec -> rec.getBab().getId() == bab.getId()).collect(toList());
            List<BabLastGroupStatus> matchesStatus = status.stream()
                    .filter(stat -> stat.getBab_id() == bab.getId()).collect(toList());
            if (!(babSettings.isEmpty())) {
                int currentGroupSum = matchesStatus.size();//看目前組別人數是否有到達bab裏頭設定的人數
                int peoples = bab.getPeople();
                if (currentGroupSum == 0 || currentGroupSum != peoples) {
                    /*
                    Insert an empty status
                    BabSettingHistory in allBabSettings are proxy object generate by hibernate
                    Can't transform to json by google.Gson directly
                     */
                    babSettings.forEach((setting) -> {
                        JSONObject obj = new JSONObject();
                        obj.put("tagName", setting.getTagName().getName());
                        obj.put("station", setting.getStation());
                        transBabData.put(obj);
                    });

                    isSomeBabUnderStandard = true;

                } else {
                    BabLastGroupStatus maxStatus = matchesStatus.stream()
                            .max((p1, p2) -> Double.compare(p1.getDiff(), p2.getDiff())).get();
                    double diffTimeSum = matchesStatus.stream().mapToDouble(BabLastGroupStatus::getDiff).sum();

                    boolean isUnderBalance = checkIsUnderBalance(bab, maxStatus.getDiff(), diffTimeSum);

                    if (isUnderBalance) {
                        isSomeBabUnderStandard = true;
                    }

                    matchesStatus.stream().map((bgs) -> {
                        bgs.setIsmax(isUnderBalance && Objects.equals(bgs, maxStatus));
                        return bgs;
                    }).forEachOrdered((bgs) -> {
                        transBabData.put(new JSONObject(bgs));
                    });
                }
            }
        });
        return transBabData;
    }

    /*
        Cell logic need to separate
        max / standard = PRODUCTIVITY
     */
    private boolean checkIsUnderBalance(Bab b, double max, double sum) {
        //Alarm by the last group lineBalance
        String lineTypeName = b.getLine().getLineType().getName();
        switch (lineTypeName) {
            case "ASSY":
                double aBaln = lineBalanceService.caculateLineBalance(max, sum, b.getPeople());

                log.debug("bab_id {} / Max: {} / Sum: {} / BANANCE: {} / STANDARD: {}", b.getId(), max,
                        sum, aBaln, ASSY_STANDARD);

                return (Double.compare(aBaln, ASSY_STANDARD) < 0);
            case "Packing":
                double pBaln = lineBalanceService.caculateLineBalance(max, sum, b.getPeople());

                log.debug("bab_id {} / Max: {} / Sum: {} / BANANCE: {} / STANDARD: {}", b.getId(), max,
                        sum, pBaln, PKG_STANDARD);

                return (Double.compare(pBaln, PKG_STANDARD) < 0);
            default:
                return false;
        }
    }

    private void babDataToMap(JSONObject avgs) {
        if (avgs != null) {
            JSONArray sensorDatas = avgs.getJSONArray("data");
            if (sensorDatas.length() != 0) {
                initMap();
                for (int i = 0, length = sensorDatas.length(); i < length; i++) {
                    JSONObject sensorData = sensorDatas.getJSONObject(i);
                    if (sensorData.has("ismax") && sensorData.getBoolean("ismax")) {
                        String tagName = sensorData.getString("tagName");
                        tagName = tagName.replace("-S-", "-L-");
                        dataMap.put(tagName, super.ALARM_SIGN); //0的資料不覆蓋節省效率
                    }
                }
            }
        }
    }

    @Override
    public void initAlarmSign() {
        List l = almService.findAll();
        if (!l.isEmpty()) {
            almService.delete(l);
        }
        almService.insert(this.mapToAlarmSign(dataMap));
    }

    @Override
    public void setAlarmSign(List l) {
        almService.update(l);
    }

    @Override
    public void resetAlarmSign() {
        almService.reset();
    }

    @Override
    public void setAlarmSignToTestingMode() {
        almService.AlarmToTestingMode();
    }

    @Override
    protected List mapToAlarmSign(Map map) {
        List l = new ArrayList();
        if (map != null && !map.isEmpty()) {
            Iterator it = map.keySet().iterator();
            while (it.hasNext()) {
                Object key = it.next();
                String tableId = key.toString();
                int action = (int) map.get(key);
                l.add(new AlarmBabAction(tableId, action));
            }
        }
        return l;
    }
}
