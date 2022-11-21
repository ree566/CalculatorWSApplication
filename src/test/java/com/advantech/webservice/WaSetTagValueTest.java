/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webservice;

import com.advantech.facade.BabLineTypeFacade;
import com.advantech.facade.BabLineTypeFacade2;
import com.advantech.facade.TestLineTypeFacade2;
import com.advantech.model.db1.AlarmBabAction;
import com.advantech.model.db1.AlarmDO;
import com.advantech.model.db1.AlarmTestAction;
import com.advantech.model.db1.TagNameComparison;
import com.advantech.service.db1.AlarmBabActionService;
import com.advantech.service.db1.AlarmDOService;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import static org.junit.Assert.*;
import org.quartz.JobExecutionException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class WaSetTagValueTest {

    private String uriSetTagValue = "http://172.20.128.235/WaWebService/Json/SetTagValue/DongHuSystem";
    private String uriGetTagValue = "http://172.20.128.235/WaWebService/Json/GetTagValue/DongHuSystem";
    private String username = "admin";
    private String password = "";

    @Autowired
    private AlarmDOService alarmDOService;

    @Autowired
    private AlarmBabActionService babService;

    @Autowired
    private WaSetTagValue waSetTagValue;
    @Autowired
    private WaGetTagValue waGetTagValue;

    @Autowired
    @Qualifier("babLineTypeFacade2")
    private BabLineTypeFacade2 bF;

    @Autowired
    @Qualifier("testLineTypeFacade2")
    private TestLineTypeFacade2 tF;

    @Test
    @Transactional
    @Rollback(true)
    public void testBabLineTypeFacade() throws JobExecutionException {
//        String st = "";
//        try {
//            AlarmDO rs = alarmDOService.findByPrimaryKey("LD-L-8");// key LD-L-8 one to many
//            st = rs.getCorrespondDO();
//            System.out.println("AlarmDO is : " + st);
//        } catch (Exception ex) {
//            throw ex;
////            System.out.println("Exception is : " + ex);
//        }
//        log.log(Level.INFO, "test");

        List<AlarmBabAction> alarmActions = Arrays.asList(
                new AlarmBabAction("T17", 0),
                new AlarmBabAction("PKG_L2-L-1", 0));
        waGetTagValue.initActiveTagNodes();
        bF.setAlarmSign(alarmActions);
//        bF.initMap();
//        bF.initAlarmSign();
//        List l = bF.mapToAlarmSign(bF.getMap());
//        bF.setAlarmSign(l);
//        waGetTagValue.initActiveTagNodes();
    }

//    @Test
//    @Transactional
//    @Rollback(true)
    public void resetAlarmSign() {
//        //from table Alm_BABAction in DB
//        List<AlarmBabAction> alarmBabs = babService.findAll();
        List<String> tableIds = new ArrayList<>();
//        alarmBabs.forEach(item -> {
//            tableIds.add(item.getTableId());
//        });
        tableIds.add("L2-L-1");
        List<AlarmDO> lDO = alarmDOService.findDOByTables(tableIds);
        System.out.println("findDOByTables.size=======" + lDO.size());

        waGetTagValue.initActiveTagNodes();
        Map tagNodes = WaGetTagValue.getMap();// static map with active Tags while DataBaseInit.java
        List<WaSetTagRequestModel> requestModels = lDO.stream()
                .filter(e -> tagNodes.containsKey(e.getCorrespondDO()))
                .map(alarmDo -> new WaSetTagRequestModel(alarmDo.getCorrespondDO(), 0))
                .collect(Collectors.toList());

        waSetTagValue.exchange(requestModels);

//        //from table LS_TagNameComparison in DB
//        List<WaSetTagRequestModel> requestModels = new ArrayList<>();
//        List<TagNameComparison> tagNames = tagNameService.findAll();
//        for (TagNameComparison item : tagNames) {
//            String TagName = item.getId().getOrginTagName().getName();
//            if (TagName.contains(":DI")) {
//                TagName = TagName.replace(":DI", ":DO").trim();
//                requestModels.add(new WaSetTagRequestModel(TagName, 0));
//            }
//        }
    }

//    @Test
    public void getTagValue() {

        waGetTagValue.initActiveTagNodes();
        System.out.println("map.size:= " + waGetTagValue.getMap().size());
    }

    /* POST */
    public void setTagValueSubList(String param) {

        Objects.requireNonNull(param);

//        int arrLen = 10;
//        List<List<WaSetTagRequestModel>> subL = Lists.partition(l, arrLen);
//        subL.forEach(c -> {
//            waSetTagValue.exchange(c);
//        });
//        System.out.println("getJsonString" + waSetTagValue.getJsonString());
    }

//    @Test
    public void setTagValue() {
        List<AlarmBabAction> alarmBabs = new ArrayList<>();
        alarmBabs.add(new AlarmBabAction("L1-2-L-5", 1));

        waGetTagValue.initActiveTagNodes();
        bF.setAlarmSign(alarmBabs);

        if (alarmBabs != null) {
//            List<AlarmDO> listDO = findDOByTables(alarmBabs);
//            Map<String, String> mapTablesDOs = listDO.stream()
//                    .collect(Collectors.toMap(AlarmDO::getProcessName, AlarmDO::getCorrespondDO));
//            System.out.println("mapTablesDOs.size=====" + mapTablesDOs.size());
//
//            List<WaSetTagRequestModel> requestModels = new ArrayList<>();
//            alarmBabs.forEach(e -> {
////            alarmBabs.stream().filter(e -> {
//                if (mapTablesDOs.containsKey(e.getTableId())) {
//                    requestModels.add(
//                            new WaSetTagRequestModel(mapTablesDOs.get(e.getTableId()), e.getAlarm())
//                    );
//                } 
//            });
//            System.out.println("requestModels.size=====" + requestModels.size());
//
//            //filter
//            waGetTagValue.initActiveTagNodes();
//            Map tagNodes = WaGetTagValue.getMap();
//            requestModels.stream().peek(e -> {
//                tagNodes.containsKey(e.getName());
//            }).collect(Collectors.toList());
//            System.out.println("requestModels.size=====" + requestModels.size());
//        exchange(l);
//        System.out.println("getJsonString" + rb.getJsonString());
        }
    }
}
