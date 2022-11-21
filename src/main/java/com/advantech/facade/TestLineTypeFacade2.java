/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.facade;

import com.advantech.webservice.WaSetTagRequestModel;
import com.advantech.webservice.WaSetTagValue;
import com.advantech.model.db1.AlarmDO;
import com.advantech.model.db1.AlarmTestAction;
import com.advantech.service.db1.AlarmDOService;
import com.advantech.service.db1.AlarmTestActionService;
import com.advantech.webservice.WaGetTagValue;
import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
@Primary
public class TestLineTypeFacade2 extends TestLineTypeFacade {

    @Autowired
    private AlarmDOService alarmDOService;

    @Autowired
    @Qualifier("alarmTestActionService")
    private AlarmTestActionService almService;

    @Autowired
    private WaSetTagValue waSetTagValue;

    @Override
    public void setAlarmSign(List l) {

        List<AlarmTestAction> alarmActions = (List<AlarmTestAction>) l;

        if (alarmActions != null) {
            //find DO
            List<AlarmDO> listDO = findDOByTables(alarmActions);
            //get active DO and alarmValue
            Map tagNodes = WaGetTagValue.getMap();
            List<WaSetTagRequestModel> requestModels = Streams
                    .zip(listDO.stream(), alarmActions.stream(),
                            (tag, alarm) -> new WaSetTagRequestModel(tag.getCorrespondDO(), alarm.getAlarm()))
                    .filter(model -> tagNodes.containsKey(model.getName()))
                    .collect(Collectors.toList());

            waSetTagValue.exchange(requestModels);
        }
    }

    @Override
    public void resetAlarmSign() {
        List<AlarmTestAction> alarmActions = almService.findAll();
        List<AlarmDO> listDO = findDOByTables(alarmActions);

        //filter
        Map tagNodes = WaGetTagValue.getMap();
        List<WaSetTagRequestModel> requestModels = listDO.stream()
                .filter(e -> tagNodes.containsKey(e.getCorrespondDO()))
                .map(alarmDo -> new WaSetTagRequestModel(alarmDo.getCorrespondDO(), 0))
                .collect(Collectors.toList());

        //reset
        waSetTagValue.exchange(requestModels);
    }

    private List<AlarmDO> findDOByTables(List<AlarmTestAction> alarmActions) {
        //find table ID
        List<String> tableIds = new ArrayList<>();
        alarmActions.forEach(item -> {
            tableIds.add(item.getTableId());
        });

        //get correspond DO
        return alarmDOService.findDOByTables(tableIds);
    }
}
