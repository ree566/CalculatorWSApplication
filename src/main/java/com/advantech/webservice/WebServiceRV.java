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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tempuri.RvResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Wei.Cheng
 */
@Component
public class WebServiceRV {

    private static final Logger log = LoggerFactory.getLogger(WebServiceRV.class);

    @Autowired
    private WsClient client;

    @Autowired
    private MultiWsClient mClient;

    private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    //Get data from WebService
    private List<Object> getWebServiceData(String queryString) {
        RvResponse response = client.simpleRvSendAndReceive(queryString);
        RvResponse.RvResult result = response.getRvResult();
        return result.getAny();
    }

    private Document getWebServiceDataForDocument(String queryString) {
        List data = getWebServiceData(queryString);
        return ((Node) data.get(1)).getOwnerDocument();
    }

    private List getKanbanUsers() {
        String queryString = "<root>"
                + "<METHOD ID='ETLSO.QryProductionKanban4Test'/>"
                + "<KANBANTEST>"
                + "<STATION_ID>4,122,124,11,3,5,6,32,30,134,151,04,105</STATION_ID>"
                + "</KANBANTEST>"
                + "</root>";
        return this.getWebServiceData(queryString);
    }

    public List<String> getKanbanUsersForString() throws IOException, TransformerConfigurationException, TransformerException {
        String queryString = "<root>"
                + "<METHOD ID='ETLSO.QryProductionKanban4Test'/>"
                + "<KANBANTEST>"
                + "<STATION_ID>4,122,124,11,3,5,6,32,30,134,151,04,105</STATION_ID>"
                + "</KANBANTEST>"
                + "</root>";
        return client.getFormatWebServiceData(queryString);
    }

    public String getKanbanWorkId(String jobnumber) {
        String today = getToday();
        String queryString = "<root><METHOD ID='WMPSO.QryWorkManPowerCard001'/><WORK_MANPOWER_CARD><WORK_ID>-1</WORK_ID><LINE_ID>-1</LINE_ID><STATION_ID>-1</STATION_ID><FACTORY_NO></FACTORY_NO><UNIT_NO></UNIT_NO>"
                + "<USER_NO>" + jobnumber + "</USER_NO>"
                + "<CARD_FLAG>1</CARD_FLAG>"
                + "<START_DATE>" + today + "</START_DATE>"
                + "<END_DATE>" + today + "</END_DATE>"
                + "</WORK_MANPOWER_CARD></root>";

        Document doc = this.getWebServiceDataForDocument(queryString);
        String childTagName = "WORK_ID";
        Element rootElement = doc.getDocumentElement();
        String requestQueueName = getString(childTagName, rootElement);
        return requestQueueName;
    }

    public String getModelNameByPo(String po, Factory f) {
        String queryString = "<root><METHOD ID='WIPSO.QryWipAtt001'/><WIP_ATT><WIP_NO>"
                + po
                + "</WIP_NO><ITEM_NO></ITEM_NO></WIP_ATT></root>";

        RvResponse response = mClient.simpleRvSendAndReceive(queryString, f);
        RvResponse.RvResult result = response.getRvResult();
        List l = result.getAny();

        Document doc = ((Node) l.get(1)).getOwnerDocument();
        String childTagName = "ITEM_NO";
        Element rootElement = doc.getDocumentElement();
        String requestQueueName = getString(childTagName, rootElement);
        return requestQueueName;
    }

    public String getPoByBarcode(String barcode, Factory f) {
        String queryString = "<root><METHOD ID='WIPSO.QryWipBarcode003'/><WIP_BARCODE><BARCODE_NO>"
                + barcode
                + "</BARCODE_NO></WIP_BARCODE></root>";

        RvResponse response = mClient.simpleRvSendAndReceive(queryString, f);
        RvResponse.RvResult result = response.getRvResult();
        List l = result.getAny();

        Document doc = ((Node) l.get(1)).getOwnerDocument();
        String childTagName = "WIP_NO";
        Element rootElement = doc.getDocumentElement();
        String requestQueueName = getString(childTagName, rootElement);
        return requestQueueName;
    }

    public UserOnMes getMESUser(String jobnumber) {
        try {
            String queryString = "<root><METHOD ID='PLBSO.QryLogion'/><USER_INFO><USER_NO>"
                    + jobnumber
                    + "</USER_NO><PASSWORD></PASSWORD><STATUS>A</STATUS></USER_INFO></root>";

            List l = this.getWebServiceData(queryString);
            Document doc = ((Node) l.get(1)).getOwnerDocument();
            //Skip the <diffgr:diffgram> tag, read QryData tag directly.
            Node node = doc.getFirstChild().getFirstChild().getFirstChild();

            Object o = this.unmarshalFromList(node, UserOnMes.class);

            return o == null ? null : (UserOnMes) o;
        } catch (Exception ex) {
            log.error(ex.toString());
            return null;
        }
    }

    public List<UserInfoOnMes> getUsersInfoOnMes(Factory f) {

        try {
            String queryString
                    = "<root><METHOD ID='SYSSO.QryUserInfo001'/><USERS><USER_ID>-1</USER_ID><DEPT_ID>-1</DEPT_ID>"
                    + "<STATUS>A</STATUS><UNIT_NO></UNIT_NO><LINE_ID>-1</LINE_ID></USERS></root>";

            RvResponse response = mClient.simpleRvSendAndReceive(queryString, f);
            RvResponse.RvResult result = response.getRvResult();
            List l = result.getAny();

            Document doc = ((Node) l.get(1)).getOwnerDocument();
            //Skip the <diffgr:diffgram> tag, read QryData tag directly.
            Node node = doc.getFirstChild().getFirstChild();

            Object o = this.unmarshalFromList(node, UsersInfoOnMes.class);
            return o == null ? new ArrayList() : ((UsersInfoOnMes) o).getQryData();
        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

    public List<PassStationRecord> getPassStationRecords(String po, final Factory f) {
        String stations = "16";

        try {
            String queryString
                    = "<root><METHOD ID='ETLSO.QryT_SnPassTime001'/><WIP_INFO><WIP_NO>"
                    + po
                    + "</WIP_NO><UNIT_NO></UNIT_NO><LINE_ID></LINE_ID><STATION_ID>"
                    + stations
                    + "</STATION_ID></WIP_INFO></root>";

            RvResponse response = mClient.simpleRvSendAndReceive(queryString, f);
            RvResponse.RvResult result = response.getRvResult();
            List l = result.getAny();

            Document doc = ((Node) l.get(1)).getOwnerDocument();
            //Skip the <diffgr:diffgram> tag, read QryData tag directly.
            Node node = doc.getFirstChild().getFirstChild();

            Object o = this.unmarshalFromList(node, PassStationRecords.class);
            return o == null ? new ArrayList() : ((PassStationRecords) o).getQryData();
        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

    public List<TestRecord> getTestLineTypeRecords() {
        try {
            List l = this.getKanbanUsers();
            Document doc = ((Node) l.get(1)).getOwnerDocument();
            //Skip the <diffgr:diffgram> tag, read QryData tag directly.
            Node node = doc.getFirstChild().getFirstChild();

            Object o = this.unmarshalFromList(node, TestRecords.class);

            return o == null ? new ArrayList() : ((TestRecords) o).getQryData();
        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

    public List<TestPassStationDetail> getTestPassStationDetails(List<String> jobnumbers, Section section, int station, DateTime sD, DateTime eD, final Factory f) {

        try {
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
                    + "<WERKS>TWM3</WERKS>"
                    + "</RPT404>"
                    + "</root>";

            RvResponse response = mClient.simpleRvSendAndReceive(queryString, f);
            RvResponse.RvResult result = response.getRvResult();
            List l = result.getAny();

            Document doc = ((Node) l.get(1)).getOwnerDocument();
            //Skip the <diffgr:diffgram> tag, read QryData tag directly.
            Node node = doc.getFirstChild().getFirstChild();

            Object o = this.unmarshalFromList(node, TestPassStationDetails.class);
            return o == null ? new ArrayList() : ((TestPassStationDetails) o).getQryData();
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

        String unit = "B";

        try {
            String queryString
                    = "<root>"
                    + "<METHOD ID='KPISO.QryRPT404'/>"
                    + "<RPT404>"
                    + "<WERKS>TWM3</WERKS>"
                    + "<UNIT_NO>" + unit + "</UNIT_NO>"
                    + "<START_DATE>" + fmt.print(sD) + "</START_DATE>"
                    + "<END_DATE>" + fmt.print(eD) + "</END_DATE>"
                    + "<LINE_ID></LINE_ID>"
                    + "</RPT404>"
                    + "</root>";

            RvResponse response = mClient.simpleRvSendAndReceive(queryString, f);
            RvResponse.RvResult result = response.getRvResult();
            List l = result.getAny();

            Document doc = ((Node) l.get(1)).getOwnerDocument();
            //Skip the <diffgr:diffgram> tag, read QryData tag directly.
            Node node = doc.getFirstChild().getFirstChild();

            Object o = this.unmarshalFromList(node, MesPassCountRecords.class);
            return o == null ? new ArrayList() : ((MesPassCountRecords) o).getQryData();
        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

    public List<RptStationQty> getRptStationQtys(DateTime sD, DateTime eD, int mesLineId, final Factory f) {

        try {
            String queryString
                    = "<root>"
                    + "<METHOD ID='RPTSO.QryRptStationQty001'/>"
                    + "<STATION_QTY>"
                    + "<START_TIME>" + fmt.print(sD) + "</START_TIME>"
                    + "<END_TIME>" + fmt.print(eD) + "</END_TIME>"
                    + "<UNIT_NO/>"
                    + "<STATION_ID>" + mesLineId + "</STATION_ID>"
                    + "<WERKS>TWM3</WERKS>"
                    + "<ITEM_NO/>"
                    + "</STATION_QTY>"
                    + "</root>";

            RvResponse response = mClient.simpleRvSendAndReceive(queryString, f);
            RvResponse.RvResult result = response.getRvResult();
            List l = result.getAny();

            Document doc = ((Node) l.get(1)).getOwnerDocument();
            //Skip the <diffgr:diffgram> tag, read QryData tag directly.
            Node node = doc.getFirstChild().getFirstChild();

            Object o = this.unmarshalFromList(node, RptStationQtys.class);
            return o == null ? new ArrayList() : ((RptStationQtys) o).getQryData();
        } catch (JAXBException ex) {
            log.error(ex.toString());
            return new ArrayList();
        }
    }

    private Object unmarshalFromList(Node node, Class clz) throws JAXBException {

        //Unmarshal the data into javaObject.
        JAXBContext jc = JAXBContext.newInstance(clz);
        Unmarshaller u = jc.createUnmarshaller();

        return node == null ? null : u.unmarshal(node);
    }

    private String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();
            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }
        return null;
    }

    private String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
