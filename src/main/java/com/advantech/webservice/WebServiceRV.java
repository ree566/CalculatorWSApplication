/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.webservice;

import com.advantech.model.db1.MesPassCountRecord;
import com.advantech.model.db1.MesPassCountRecords;
import com.advantech.model.db1.PassStationRecord;
import com.advantech.model.db1.PassStationRecords;
import com.advantech.model.db1.RptStationQty;
import com.advantech.model.db1.RptStationQtys;
import com.advantech.model.db1.Test;
import com.advantech.model.db1.TestPassStationDetail;
import com.advantech.model.db1.TestPassStationDetails;
import com.advantech.model.db1.TestRecord;
import com.advantech.model.db1.TestRecords;
import com.advantech.model.db1.UserInfoOnMes;
import com.advantech.model.db1.UserOnMes;
import com.advantech.model.db1.UsersInfoOnMes;
import com.advantech.webservice.mes.Section;
import com.advantech.webservice.mes.SimpleWebServiceRV;
import com.advantech.webservice.mes.WsClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Wei.Cheng
 */
@Component
public class WebServiceRV extends SimpleWebServiceRV {

    private static final Logger log = LoggerFactory.getLogger(WebServiceRV.class);

    @Autowired
    private MultiWsClient mClient;

    private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    @PostConstruct
    private void init() {
    }

    public List getKanbanUsers(Factory f) {
        super.setWsClient(mClient.getClient(f));
        String queryString = "<root>"
                + "<METHOD ID='ETLSO.QryProductionKanban4Test'/>"
                + "<KANBANTEST>"
                + "<STATION_ID>4,122,124,11,3,5,6,32,30,134,151,04,105</STATION_ID>"
                + "</KANBANTEST>"
                + "</root>";
        return super.getWebServiceData(queryString);
    }

    public List<String> getKanbanUsersForString(Factory f) throws IOException, TransformerConfigurationException, TransformerException {
        WsClient client = mClient.getClient(f);
        String queryString = "<root>"
                + "<METHOD ID='ETLSO.QryProductionKanban4Test'/>"
                + "<KANBANTEST>"
                + "<STATION_ID>4,122,124,11,3,5,6,32,30,134,151,04,105</STATION_ID>"
                + "</KANBANTEST>"
                + "</root>";
        return client.getFormatWebServiceData(queryString);
    }

    public String getKanbanWorkId(String jobnumber, Factory f) {
        super.setWsClient(mClient.getClient(f));
        String dt = fmt.print(new DateTime());
        String queryString = "<root><METHOD ID='WMPSO.QryWorkManPowerCard001'/><WORK_MANPOWER_CARD><WORK_ID>-1</WORK_ID><LINE_ID>-1</LINE_ID><STATION_ID>-1</STATION_ID><FACTORY_NO></FACTORY_NO><UNIT_NO></UNIT_NO>"
                + "<USER_NO>" + jobnumber + "</USER_NO>"
                + "<CARD_FLAG>1</CARD_FLAG>"
                + "<START_DATE>" + dt + "</START_DATE>"
                + "<END_DATE>" + dt + "</END_DATE>"
                + "</WORK_MANPOWER_CARD></root>";
        String childTagName = "WORK_ID";
        return super.getFieldValue(queryString, childTagName);
    }

    public String getModelNameByPo(String po, Factory f) {
        super.setWsClient(mClient.getClient(f));
        String queryString = "<root><METHOD ID='WIPSO.QryWipAtt001'/><WIP_ATT><WIP_NO>"
                + po
                + "</WIP_NO><ITEM_NO></ITEM_NO></WIP_ATT></root>";
        String childTagName = "ITEM_NO";
        return super.getFieldValue(queryString, childTagName);
    }

    public String getPoByBarcode(String barcode, Factory f) {
        super.setWsClient(mClient.getClient(f));
        String queryString = "<root><METHOD ID='WIPSO.QryWipBarcode003'/><WIP_BARCODE><BARCODE_NO>"
                + barcode
                + "</BARCODE_NO></WIP_BARCODE></root>";
        String childTagName = "WIP_NO";
        return super.getFieldValue(queryString, childTagName);
    }

    public UserOnMes getMESUser(String jobnumber, Factory f) {
        try {
            super.setWsClient(mClient.getClient(f));
            String queryString = "<root><METHOD ID='PLBSO.QryLogion'/><USER_INFO><USER_NO>"
                    + jobnumber
                    + "</USER_NO><PASSWORD></PASSWORD><STATUS>A</STATUS></USER_INFO></root>";

            return super.getMarshalResult(queryString, UserOnMes.class);
        } catch (Exception ex) {
            log.error(ex.toString());
            return null;
        }
    }

    public List<UserInfoOnMes> getUsersInfoOnMes(Factory f) {
        try {
            super.setWsClient(mClient.getClient(f));
            String queryString
                    = "<root><METHOD ID='SYSSO.QryUserInfo001'/><USERS><USER_ID>-1</USER_ID><DEPT_ID>-1</DEPT_ID>"
                    + "<STATUS>A</STATUS><UNIT_NO></UNIT_NO><LINE_ID>-1</LINE_ID></USERS></root>";
            return super.getMarshalResults(queryString, UsersInfoOnMes.class);
        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

    public List<PassStationRecord> getPassStationRecords(String po, int mesStationId, final Factory f) {
        try {
            super.setWsClient(mClient.getClient(f));
            String queryString
                    = "<root><METHOD ID='ETLSO.QryT_SnPassTime001'/><WIP_INFO><WIP_NO>"
                    + po
                    + "</WIP_NO><UNIT_NO></UNIT_NO><LINE_ID></LINE_ID><STATION_ID>"
                    + mesStationId
                    + "</STATION_ID></WIP_INFO></root>";

            return super.getMarshalResults(queryString, PassStationRecords.class);
        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

    public List<TestRecord> getTestLineTypeRecords(Factory f) {
        try {
            super.setWsClient(mClient.getClient(f));
            String queryString = "<root>"
                    + "<METHOD ID='ETLSO.QryProductionKanban4Test'/>"
                    + "<KANBANTEST>"
                    + "<STATION_ID>4,122,124,11,3,5,6,32,30,134,151,04,105</STATION_ID>"
                    + "</KANBANTEST>"
                    + "</root>";

            return super.getMarshalResults(queryString, TestRecords.class);

        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

    public List<TestPassStationDetail> getTestPassStationDetails(List<String> jobnumbers, Section section, int station, DateTime sD, DateTime eD, final Factory f) {
        try {
            super.setWsClient(mClient.getClient(f));

            String jobnumberStr = String.join(",", jobnumbers);

            String queryString
                    = "<root>"
                    + "<METHOD ID='RPTSO.QryKPIUserPassStationDetail'/>"
                    + "<RPT404>"
                    + "<UNIT_NO>" + section.getCode() + "</UNIT_NO>"
                    + "<STATION_ID>" + station + "</STATION_ID>"
                    + "<USER_NO>" + jobnumberStr + "</USER_NO>"
                    + "<START_DATE>" + fmt.print(sD) + "</START_DATE>"
                    + "<END_DATE>" + fmt.print(eD) + "</END_DATE>"
                    + "<WERKS>TW" + f.token() + "</WERKS>"
                    + "</RPT404>"
                    + "</root>";

            return super.getMarshalResults(queryString, TestPassStationDetails.class);
        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

    public List<TestPassStationDetail> getTestPassStationDetails2(List<Test> users, Section section, int station, DateTime sD, DateTime eD, final Factory f) {
        List<String> jobnumbers = users.stream().map(t -> "'" + t.getUserId() + "'").collect(Collectors.toList());
        return getTestPassStationDetails(jobnumbers, section, station, sD, eD, f);
    }

    public List<MesPassCountRecord> getMesPassCountRecords(DateTime sD, DateTime eD, final Factory f) {
        try {
            super.setWsClient(mClient.getClient(f));
            String unit = "B";
            String queryString
                    = "<root>"
                    + "<METHOD ID='KPISO.QryRPT404'/>"
                    + "<RPT404>"
                    + "<WERKS>TW" + f.token() + "</WERKS>"
                    + "<UNIT_NO>" + unit + "</UNIT_NO>"
                    + "<START_DATE>" + fmt.print(sD) + "</START_DATE>"
                    + "<END_DATE>" + fmt.print(eD) + "</END_DATE>"
                    + "<LINE_ID></LINE_ID>"
                    + "</RPT404>"
                    + "</root>";

            return super.getMarshalResults(queryString, MesPassCountRecords.class);
        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

    public List<RptStationQty> getRptStationQtys(String modelName, int mesStationId, final Factory f) {
        try {
            super.setWsClient(mClient.getClient(f));
            String queryString
                    = "<root>"
                    + "<METHOD ID='RPTSO.QryRptStationQty001'/>"
                    + "<STATION_QTY>"
                    + "<START_TIME></START_TIME>"
                    + "<END_TIME></END_TIME>"
                    + "<UNIT_NO/>"
                    + "<STATION_ID>" + mesStationId + "</STATION_ID>"
                    + "<WERKS/>"
                    + "<ITEM_NO>" + modelName + "</ITEM_NO>"
                    + "</STATION_QTY>"
                    + "</root>";

            return super.getMarshalResults(queryString, RptStationQtys.class);
        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

}
