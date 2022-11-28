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
import java.util.ArrayList;
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
    private WaGetTagValue waGetTagValue;
    
    @Autowired
    private WaSetTagValue waSetTagValue;

    @Override
    public void setAlarmSign(List l) {

        List<AlarmBabAction> alarmBabs = (List<AlarmBabAction>) l;

        //find DO
        List<AlarmDO> listDO = findDOByTables(alarmBabs);

        //change list to map only including active TableIds-DOs
        Map allActiveTags = waGetTagValue.getMap();
        Map<String, String> mapTablesDOs = listDO.stream()
                .filter(e -> allActiveTags.containsKey(e.getCorrespondDO()))
                .collect(Collectors.toMap(AlarmDO::getProcessName, AlarmDO::getCorrespondDO));

        //set requestBody
        List<WaSetTagRequestModel> requestModels = new ArrayList<>();
        alarmBabs.forEach(e -> {
            if (mapTablesDOs.containsKey(e.getTableId())) {
                requestModels.add(
                        new WaSetTagRequestModel(mapTablesDOs.get(e.getTableId()), e.getAlarm())
                );
            }
        });

        waSetTagValue.exchange(requestModels);
    }

    @Override
    public void resetAlarmSign() {
        List<AlarmBabAction> alarmBabs = babService.findAll();
        List<AlarmDO> listDO = findDOByTables(alarmBabs);

        //filter
        Map allActiveTags = waGetTagValue.getMap();
        List<WaSetTagRequestModel> requestModels = new ArrayList<>();
        listDO.forEach(e -> {
            if (allActiveTags.containsKey(e.getCorrespondDO())) {
                requestModels.add(
                        new WaSetTagRequestModel(e.getCorrespondDO(), 0)
                );
            }
        });

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
