/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.webservice;

import com.advantech.model.CellLineType;
import com.advantech.model.PassStation;
import com.advantech.model.PassStationRecords;
import com.advantech.model.TestRecord;
import com.advantech.model.TestRecords;
import com.advantech.model.UserOnMes;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
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
                + "<METHOD ID='Advantech.ETL.ETL.BLL.QryProductionKanban4Test'/>"
                + "<KANBANTEST>"
                + "<STATION_ID>4,122,124,11,3,5,6,32,30,134,151,04,105</STATION_ID>"
                + "</KANBANTEST>"
                + "</root>";
        return this.getWebServiceData(queryString);
    }

    public List<String> getKanbanUsersForString() throws IOException, TransformerConfigurationException, TransformerException{
        String queryString = "<root>"
                + "<METHOD ID='Advantech.ETL.ETL.BLL.QryProductionKanban4Test'/>"
                + "<KANBANTEST>"
                + "<STATION_ID>4,122,124,11,3,5,6,32,30,134,151,04,105</STATION_ID>"
                + "</KANBANTEST>"
                + "</root>";
        return client.getFormatWebServiceData(queryString);
    }

    public String getKanbanWorkId(String jobnumber) {
        String today = getToday();
        String queryString = "<root><METHOD ID='Advantech.SFC.PBD.BLL.QryWorkManPowerCard001'/><WORK_MANPOWER_CARD><WORK_ID>-1</WORK_ID><LINE_ID>-1</LINE_ID><STATION_ID>-1</STATION_ID><FACTORY_NO></FACTORY_NO><UNIT_NO></UNIT_NO>"
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

    public String getModelnameByPo(String po) {
        String queryString = "<root><METHOD ID='Advantech.QAM.IPQ.BLL.QryWipAtt001'/><WIP_ATT><WIP_NO>"
                + po
                + "</WIP_NO><ITEM_NO></ITEM_NO></WIP_ATT></root>";
        Document doc = this.getWebServiceDataForDocument(queryString);
        String childTagName = "ITEM_NO";
        Element rootElement = doc.getDocumentElement();
        String requestQueueName = getString(childTagName, rootElement);
        return requestQueueName;
    }

    public UserOnMes getMESUser(String jobnumber) {
        try {
            String queryString = "<root><METHOD ID='Advantech.SFC.SNM.BLL.QryLogion'/><USER_INFO><USER_NO>"
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

    public List<PassStation> getPassStationRecords(String po, String type) {
        String stations;
        if (CellLineType.BAB.toString().equals(type)) {
            stations = "'2','20'";
        } else if (CellLineType.PKG.toString().equals(type)) {
            stations = "'53','28'";
        } else {
            return new ArrayList();
        }

        try {
            String queryString
                    = "<root><METHOD ID='Advantech.ETL.ETL.BLL.QryT_SnPassTime001'/><WIP_INFO><WIP_NO>"
                    + po
                    + "</WIP_NO><UNIT_NO></UNIT_NO><LINE_ID></LINE_ID><STATION_ID>"
                    + stations
                    + "</STATION_ID></WIP_INFO></root>";

            List l = this.getWebServiceData(queryString);
            Document doc = ((Node) l.get(1)).getOwnerDocument();
            //Skip the <diffgr:diffgram> tag, read QryData tag directly.
            Node node = doc.getFirstChild().getFirstChild();

            Object o = this.unmarshalFromList(node, PassStationRecords.class);
            return o == null ? new ArrayList() : ((PassStationRecords) o).getQryData();
        } catch (Exception ex) {
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
        } catch (Exception ex) {
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
