/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 取得資料庫資訊用
 */
package com.advantech.quartzJob;

import com.advantech.endpoint.Endpoint2;
import com.advantech.facade.BabLineTypeFacade2;
import com.advantech.facade.TestLineTypeFacade2;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Wei.Cheng
 */
@Component
public class PollingBabAndTestResult implements EndpointPollingJob{

    private static final Logger log = LoggerFactory.getLogger(PollingBabAndTestResult.class);

    @Autowired
    private TestLineTypeFacade2 tF;

    @Autowired
    private BabLineTypeFacade2 bF;

    @Autowired
    private Endpoint2 socket;

    //抓取資料庫資料並廣播
    @Override
    public void dataBrocast() {
        /*
         ERROR - java.sql.SQLException: 
         Transaction (Process ID 69) was deadlocked on lock resources with another process 
         and has been chosen as the deadlock victim. Rerun the transaction. 
         Query: select * from LS_GetSenRealTime Parameters: []
         */
        try {
            socket.sendAll(getData());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getData() {
//        throw new RuntimeException("This is a testing sql exception...");
        return new JSONArray().put(tF.getJSONObject())
                .put(bF.getJSONObject()).toString();
    }
}
