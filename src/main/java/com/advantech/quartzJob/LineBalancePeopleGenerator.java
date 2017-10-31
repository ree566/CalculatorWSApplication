/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 
 */
package com.advantech.quartzJob;

import com.advantech.entity.BAB;
import com.advantech.helper.CronTrigMod;
import com.advantech.helper.PropertiesReader;
import com.advantech.service.BABService;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.json.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author Wei.Cheng Job separate by class NumLamp, caculate the lineBalance
 * between testLine and babLine.
 */
public class LineBalancePeopleGenerator extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(LineBalancePeopleGenerator.class);

    @Autowired
    private BABService babService;
    
    private int numLampMaxTestRequiredPeople, numLampGroupStart, numLampGroupEnd, numLampSpecCuttingGroup;

    private int currentGroup;

    private Double babStandard;
    private Double testStandardTime; //分鐘
    private Integer totalQuantity;

    private List<String> message;

    private DecimalFormat formatter;
    private DecimalFormat formatter2;

    private int startCountMininumQuantity, startCountMininumStandardTime, minTotalStandardTime, basicSuggestPeople = 1;

    private BAB bab;
    private JobKey currentJobKey;
    private TriggerKey currentTriggerKey;

    private NumLamp numLamp;
    
    @Autowired
    private CronTrigMod ctm;

    @PostConstruct
    public void init() {
        PropertiesReader p = PropertiesReader.getInstance();
        this.numLampMaxTestRequiredPeople = p.getNumLampMaxTestRequiredPeople();
        this.numLampGroupStart = p.getNumLampGroupStart();
        this.numLampGroupEnd = p.getNumLampGroupEnd();
        this.numLampSpecCuttingGroup = p.getNumLampSpecCuttingGroup() == 0 ? 1 : p.getNumLampSpecCuttingGroup(); //若未設定，每1組計算一次
        this.babStandard = p.getAssyStandard();
        this.startCountMininumQuantity = p.getNumLampMinQuantity();
        this.startCountMininumStandardTime = p.getNumLampMinStandardTime();
        this.minTotalStandardTime = p.getNumLampMinTotalStandardTime();

        formatter = new DecimalFormat("#.##%");
        formatter2 = new DecimalFormat("#.##");

        this.numLamp = new NumLamp();
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        this.currentTriggerKey = context.getTrigger().getKey();
        this.currentJobKey = context.getJobDetail().getKey();
        this.generateTestPeople();
    }

    private void jobSelfRemove() {
        try {
            ctm.jobPause(currentJobKey);
        } catch (SchedulerException ex) {
            log.error(ex.toString());
        }
    }

    private void generateTestPeople() {

        //初始化要回應的訊息
        message = new ArrayList();

        //檢查設定值
        if (!isSettingVaild()) {
            showAbnormalSettingMessage();
            return;
        }

        //查看目前分配到第幾組了，依照目前組別取得lineBalance
        List<Map> l = babService.getLastGroupStatus(bab.getId());
        currentGroup = l.isEmpty() ? 1 : (int) l.get(0).get("groupid");
        List<Map> balanceGroup = findCurrentLineBalance();

        //檢查組別
        if (balanceGroup.isEmpty()) {
            showProccessingMessage();
            return;
        }

        //計算組裝CT
        Integer babCT = (int) Math.floor(((BigDecimal) findMaxInList(balanceGroup)).doubleValue());

        if (!isStatusExist() || isPcsFilterCountRule()) {
            if (isStatusExist() && isGroupTheSame()) {
                return;
            } else {
                //計算人數，傳回給parent
                caculateAndReportDataToParentJob(babCT);
            }
        }

        if (testStandardTime != null && (testStandardTime < startCountMininumStandardTime || currentGroup >= numLampGroupEnd || currentGroup > totalQuantity)) {
            jobSelfRemove();
        } else {
            //Update the current group status finally anyway.
            updateCurrentGroup();

        }
    }

    private boolean isSettingVaild() {
        return totalQuantity != null && testStandardTime != null;
    }

    private void showAbnormalSettingMessage() {
        JSONObject obj = new JSONObject(bab);
        message.add((totalQuantity == null ? "TotalQuantity" : "TestStandard time") + " is not setting");
        obj.put("message", message);
        numLamp.getProcessStatus().put(bab.getLineName(), obj);
    }

    private void showProccessingMessage() {
        JSONObject obj = new JSONObject(bab);
        message.add("Waiting for first group...");
        obj.put("suggestTestPeople", 0);
        obj.put("message", message);
        numLamp.getProcessStatus().put(bab.getLineName(), obj);
    }

    private boolean isStatusExist() {
        JSONObject obj = numLamp.getProcessStatus();
        if (obj.has(bab.getLineName())) {
            JSONObject lineObj = obj.getJSONObject(bab.getLineName());
            return lineObj.has("suggestTestPeople") ? (lineObj.getInt("suggestTestPeople") != 0) : false;
        } else {
            return false;
        }
    }

    private boolean isGroupTheSame() {
        try {
            return numLamp.getProcessStatus().getJSONObject(bab.getLineName()).get("quantity").equals(currentGroup);
        } catch (Exception ex) {
            return true;
        }
    }

    //依照組別得知目前線平衡狀態
    private List<Map> findCurrentLineBalance() {
        if (currentGroup < numLampGroupStart || currentGroup < numLampSpecCuttingGroup) {
            return babService.getBABAvgsInSpecGroup(bab.getId(), 1, currentGroup);
        } else if (numLampGroupEnd < currentGroup) {
            return babService.getBABAvgsInSpecGroup(bab.getId(), numLampGroupStart, numLampGroupEnd);
        } else {
            return babService.getBABAvgsInSpecGroup(bab.getId(), numLampGroupStart, findClosetGroup());
        }
    }

    private int findClosetGroup() {
        return (currentGroup / numLampSpecCuttingGroup) * numLampSpecCuttingGroup;
    }

    private void updateCurrentGroup() {
        String lineName = bab.getLineName();
        try {
            JSONObject obj = numLamp.getProcessStatus().getJSONObject(lineName);
            if (obj != null) {
                obj.put("quantity", currentGroup);
                numLamp.getProcessStatus().put(lineName, obj);
            }
        } catch (Exception e) {
        } //Do nothing when object is not found
    }

    private void caculateAndReportDataToParentJob(Integer babCT) {

        JSONObject obj = new JSONObject(bab);

        Integer suggestPeople;

        if ((isFilterCountRule2(totalQuantity, testStandardTime) && isPcsFilterCountRule()) || (!isStatusExist() && isFilterCountRule2(totalQuantity, testStandardTime) && currentGroup >= numLampSpecCuttingGroup)) {
            suggestPeople = generatePeople1(babCT, testStandardTime);
            message.add("AssyCT: " + formatter2.format(secToMin(babCT)) + " min, T1 standard: " + formatter2.format(testStandardTime) + " min");
        } else {
            message.add("Total quantity is: " + totalQuantity + " pcs");
            message.add("T1 standard: " + formatter2.format(testStandardTime) + " min");
            suggestPeople = basicSuggestPeople;
        }

        if (testStandardTime < startCountMininumStandardTime || currentGroup >= numLampGroupEnd) {
            message.add("※Reach " + numLampGroupEnd + " pcs or T1 standard < " + formatter2.format(startCountMininumStandardTime) + " min");
            message.add("※Stop updates");
        }
        obj.put("suggestTestPeople", suggestPeople);
        obj.put("message", message);
        numLamp.getProcessStatus().put(bab.getLineName(), obj);
    }

    private boolean isFilterCountRule(Integer totalQuantity, Integer standardVal) {
        return totalQuantity > startCountMininumQuantity && standardVal >= startCountMininumStandardTime && standardVal * totalQuantity >= minTotalStandardTime;
    }

    private boolean isFilterCountRule2(Integer totalQuantity, Double standardVal) {
        return standardVal >= startCountMininumStandardTime && standardVal * totalQuantity >= minTotalStandardTime;
    }

    private boolean isPcsFilterCountRule() {
        return currentGroup % numLampSpecCuttingGroup == 0 && currentGroup >= this.numLampGroupStart && currentGroup <= this.numLampGroupEnd;
    }

    private int generatePeople(Integer maxVal, Integer standardVal) {
        Double[] balances = new Double[numLampMaxTestRequiredPeople];
        Double[] abs = new Double[numLampMaxTestRequiredPeople];
        Integer people = 1;
        Double balance;
        Integer min = 0;

        do {
            if (people == numLampMaxTestRequiredPeople) {
                return people;
            }
            balance = calculateBalance(maxVal, standardVal, people);
            int index = people - 1;
            balances[index] = balance;
            abs[index] = Math.abs(balances[index] - babStandard);
            if (abs[index] < abs[min]) {
                min = index;
            }
            people++;
        } while (balance - babStandard > 0 && people <= numLampMaxTestRequiredPeople);

        return min + 1;
    }

    private int generatePeople1(Integer maxVal, Double standardVal) {
        Integer standard = minToSec(standardVal);
        Map<Integer, Double> balanceResults = new HashMap();
        Integer people = basicSuggestPeople;
        do {
            balanceResults.put(people, calculateBalance(maxVal, standard, people));
            people++;
        } while (people <= numLampMaxTestRequiredPeople);

        balanceResults = this.sortByValue(balanceResults);

        for (Map.Entry<Integer, Double> entry : balanceResults.entrySet()) {
            message.add("People: " + entry.getKey() + " / Balance:" + formatter.format(entry.getValue()));
        }

        int bestSetupPeople = 0;
        int loopCount = 0;

        for (Map.Entry<Integer, Double> entry : balanceResults.entrySet()) {
            ++loopCount;
            if (entry.getValue() >= babStandard || loopCount == numLampMaxTestRequiredPeople) {
                bestSetupPeople = entry.getKey();
                break;
            }
        }
        return bestSetupPeople;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
                return (e1.getValue()).compareTo(e2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    //(組裝CT + (測試標工 / 人數)) / (max(組裝CT, (測試標工 / 人數)) * 2)  因為取組裝&測試的線平衡率，所以需要*2
    private Double calculateBalance(Integer maxVal, Integer standardVal, Integer people) {
        Double babCT = (double) maxVal, standard = (double) standardVal;
        Double numerator = babCT + (standard / people);
        Double denominator = findMax(babCT, standard / people) * 2;
        Double result = numerator / denominator;
        return result;
    }

    private Object findMaxInList(List<Map> l) {
        List avgs = new ArrayList();
        for (Map m : l) {
            avgs.add(m.get("average"));
        }
        return findMax(avgs);
    }

    private Object findMax(List l) {
        return l.isEmpty() ? null : Collections.max(l);
    }

    private Integer findMax(Integer... vals) {
        return (Integer) this.findMaxObj((Object[]) vals);
    }

    private Double findMax(Double... vals) {
        return (Double) this.findMaxObj((Object[]) vals);
    }

    private Object findMaxObj(Object... obj) {
        Arrays.sort(obj);
        return obj.length == 0 ? null : obj[obj.length - 1];
    }

    private int findCloset(int... numbers) {
        int myNumber = (int) Math.floor(this.babStandard * 100);
        int distance = Math.abs(numbers[0] - myNumber);
        int idx = 0;
        for (int c = 1; c < numbers.length; c++) {
            int cdistance = Math.abs(numbers[c] - myNumber);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        return numbers[idx];
    }

    private Double findCloset(Double... numbers) {
        Double myNumber = this.babStandard;
        Double distance = Math.abs(numbers[0] - myNumber);
        int idx = 0;
        for (int c = 1; c < numbers.length; c++) {
            Double cdistance = Math.abs(numbers[c] - myNumber);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        return numbers[idx];
    }

    private Integer minToSec(Integer minute) {
        return minute == null ? null : minute * 60;
    }

    private Integer minToSec(Double minute) {
        return minute == null ? null : (int) (minute * 60);
    }

    private Double secToMin(Integer second) {
        return second == null ? null : (double) (second) / 60;
    }

    private Double secToMin(Double second) {
        return second == null ? null : second / 60;
    }

    //Quartz job auto inject paramaters.
    public void setTestStandardTime(Double testStandardTime) {
        this.testStandardTime = testStandardTime;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setBab(BAB bab) {
        this.bab = bab;
    }

}
