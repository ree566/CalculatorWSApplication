/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 取得資料庫資訊用
 */
package com.advantech.quartzJob;

import com.advantech.endpoint.Endpoint2;
import com.advantech.service.BabLineTypeFacade;
import com.advantech.service.TestLineTypeFacade;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Wei.Cheng
 */
public class PollingServerParmater implements Job {

    private static final Gson gson = new Gson();
    private static final Logger log = LoggerFactory.getLogger(PollingServerParmater.class);

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        dataBrocast();
    }

    //抓取資料庫資料並廣播
    private void dataBrocast() {
        /*
         ERROR - java.sql.SQLException: 
         Transaction (Process ID 69) was deadlocked on lock resources with another process 
         and has been chosen as the deadlock victim. Rerun the transaction. 
         Query: select * from LS_GetSenRealTime Parameters: []
         */
        try {
            JSONArray arr = new JSONArray();
            arr.put(TestLineTypeFacade.getInstance().getJSONObject())
                    .put(BabLineTypeFacade.getInstance().getJSONObject());
            Endpoint2.sendAll(arr.toString());
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}