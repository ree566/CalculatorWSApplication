/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.facade;

import com.advantech.model.db1.AlarmBabAction;
import com.advantech.model.db1.AlarmDO;
import com.advantech.service.db1.AlarmBabActionService;
import com.advantech.service.db1.AlarmDOService;
import com.advantech.webservice.WaGetTagValue;
import com.advantech.webservice.WaSetTagRequestModel;
import com.advantech.webservice.WaSetTagValue;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
@Primary
public class BabLineTypeFacade2 extends BabLineTypeFacade {

    @Autowired
    private AlarmDOService alarmDOService;

    @Autowired
    private AlarmBabActionService babService;

    @Autowired
    private WaSetTagValue waSetTagValue;

    @Override
    public void setAlarmSign(List l) {

        List<AlarmBabAction> alarmBabs = (List<AlarmBabAction>) l;

        if (alarmBabs != null) {
            //find DO
            List<AlarmDO> listDO = findDOByTables(alarmBabs);

            //get active DO and alarmValue
            Map tagNodes = WaGetTagValue.getMap();// static map with active Tags while DataBaseInit.java
            List<WaSetTagRequestModel> requestModels = Streams
                    .zip(listDO.stream(), alarmBabs.stream(),
                            (tag, alarm) -> new WaSetTagRequestModel(tag.getCorrespondDO(), alarm.getAlarm()))
                    .filter(model -> tagNodes.containsKey(model.getName()))
                    .collect(Collectors.toList());

            //call webApi
            waSetTagValue.exchange(requestModels);
        }
    }

    @Override
    public void resetAlarmSign() {
        List<AlarmBabAction> alarmBabs = babService.findAll();
        List<AlarmDO> listDO = findDOByTables(alarmBabs);

        //filter
        Map tagNodes = WaGetTagValue.getMap();
        List<WaSetTagRequestModel> requestModels = listDO.stream()
                .filter(e -> tagNodes.containsKey(e.getCorrespondDO()))
                .map(alarmDo -> new WaSetTagRequestModel(alarmDo.getCorrespondDO(), 0))
                .collect(Collectors.toList());

        //reset
        waSetTagValue.exchange(requestModels);
    }

    private List<AlarmDO> findDOByTables(List<AlarmBabAction> alarmBabs) {
        //find table ID
        List<String> tableIds = alarmBabs.stream()
                .map(bab -> bab.getTableId())
                .collect(Collectors.toList());

        //get correspond DO
        return alarmDOService.findDOByTables(tableIds);
    }
}
