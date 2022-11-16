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
import com.advantech.webservice.WaTagValue;
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
            //setup correspond map with active
            Map tagNodes = WaTagValue.getMap();// static map with active Tags while application
            Map<String, String> mapTablesDOs = listDO.stream()
                    .filter(e -> {
                        return tagNodes.containsKey(e.getCorrespondDO());
                    })
                    .collect(Collectors.toMap(AlarmDO::getProcessName, AlarmDO::getCorrespondDO));

            //get DO and alarmValue
            List<WaSetTagRequestModel> requestModels = new ArrayList<>();
            alarmActions.forEach(e -> {
                if (mapTablesDOs.containsKey(e.getTableId())) {
                    requestModels.add(
                            new WaSetTagRequestModel(mapTablesDOs.get(e.getTableId()), e.getAlarm())
                    );
                }
            });
            
            waSetTagValue.exchange(requestModels);
        }
    }

    @Override
    public void resetAlarmSign() {
        List<AlarmTestAction> alarmActions = almService.findAll();
        List<AlarmDO> listDO = findDOByTables(alarmActions);

        //filter
        Map tagNodes = WaTagValue.getMap();
        List requestModels = new ArrayList<>();
        for (AlarmDO item : listDO) {
            String tagName = item.getCorrespondDO();
            if (tagNodes.containsKey(tagName)) {
                requestModels.add(new WaSetTagRequestModel(tagName, 0));
            }
        }

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
