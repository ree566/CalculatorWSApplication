/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.webservice;

import com.advantech.model.UserOnMes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.google.common.base.Preconditions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tempuri.TxResponse;

/**
 *
 * @author Wei.Cheng
 */
@Component
public class WebServiceTX {

    private static final Logger log = LoggerFactory.getLogger(WebServiceTX.class);

    @Autowired
    private WsClient client;
    
    @Autowired
    private WebServiceRV rv;

    private void sendData(String data, UploadType uploadType) {
        try {
            TxResponse response = client.simpleTxSendAndReceive(data, uploadType);
            String st = response.getTxResult();
            checkState("OK".equals(st), st);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void kanbanUserLogin(String jobnumber) {
        UserOnMes user = rv.getMESUser(jobnumber);

        checkUserIsAvailable(user);

        String lineId = "21", stationId = "-1";// 目前暫時寫死，等待有固定查詢來源

        String data = "<root><METHOD ID='Advantech.SFC.PBD.BLL.TxWorkManPowerCard001'/><WORK_MANPOWER_CARD>"
                + "<WORK_ID>-1</WORK_ID>"
                + "<LINE_ID>" + lineId + "</LINE_ID>"
                + "<STATION_ID>" + stationId + "</STATION_ID>"
                + "<FACTORY_NO>TWM3</FACTORY_NO>"
                + "<UNIT_NO>T</UNIT_NO>"
                + "<USER_NO>" + user.getUserNo() + "</USER_NO>"
                + "<USER_NAME_CH>" + user.getUserName() + "</USER_NAME_CH>"
                + "<WORK_DESC>\"\"</WORK_DESC>"
                + "<CARD_FLAG>1</CARD_FLAG>"
                + "<USER_ID>" + user.getUserId() + "</USER_ID>"
                + "</WORK_MANPOWER_CARD></root>";
        sendData(data, UploadType.INSERT);
    }

    public void kanbanUserLogout(String jobnumber) {
        String workId = rv.getKanbanWorkId(jobnumber);
        UserOnMes user = rv.getMESUser(jobnumber);

        checkUserIsAvailable(user);

        String data = "<root><METHOD ID='Advantech.SFC.PBD.BLL.TxWorkManPowerCard001'/><WORK_MANPOWER_CARD>"
                + "<WORK_ID>" + workId + "</WORK_ID>"
                + "<LINE_ID>-1</LINE_ID>"
                + "<STATION_ID>-1</STATION_ID>"
                + "<FACTORY_NO></FACTORY_NO>"
                + "<UNIT_NO></UNIT_NO>"
                + "<USER_NO></USER_NO>"
                + "<USER_NAME_CH></USER_NAME_CH>"
                + "<WORK_DESC></WORK_DESC>"
                + "<CARD_FLAG>-1</CARD_FLAG>"
                + "<USER_ID>" + user.getUserId() + "</USER_ID>" //get userid from second xml
                + "</WORK_MANPOWER_CARD></root>";
        sendData(data, UploadType.UPDATE);
    }

    private void checkUserIsAvailable(UserOnMes user) {
        checkArgument(user != null && user.getUserId() != null && user.getUserNo() != null, "The user is not exist.");
    }

}
