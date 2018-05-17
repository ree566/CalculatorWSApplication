/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import static java.lang.System.out;
import java.util.List;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Wei.Cheng
 */
public class WebServiceRVTest {

    private final String testJobnumber = "A-TEST";

    public WebServiceRVTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getKanbanUsersForString method, of class WebServiceRV.
     */
    @Test
    public void testWS1() throws Exception {

        out.println("中文測試");
//
//        List<String> notExpResult = null;
//        List<String> notExpResult2 = new ArrayList();
//
//        String str = "<root><METHOD ID='PLBSO.QryLogion'/><USER_INFO><USER_NO>" + testJobnumber + "</USER_NO><PASSWORD></PASSWORD><STATUS>A</STATUS></USER_INFO></root>";
//
//        List<String> result = WebServiceRV.getInstance().getKanbanUsersForString(str);
//
//        assertNotEquals(notExpResult, result);
//        assertNotEquals(notExpResult2, result);
//
//        this.printXml(result);
    }
    /**
     * Test of getKanbanUsersForString method, of class WebServiceRV.
     */
    @Test
    public void testWS2() throws Exception {
//        out.println("testWS2");
//        String childTagName = "USER_NO";
//
//        List<String> notExpResult = null;
////        List<String> notExpResult2 = new ArrayList();
//
//        
//
//        Document result = WebServiceRV.getInstance().getKanbanUserInHistory(testJobnumber);
//
//        assertNotEquals(notExpResult, result);
////        assertNotEquals(notExpResult2, result);
//
//        Element rootElement = result.getDocumentElement();
//        String requestQueueName = getString(childTagName, rootElement);
//
////        out.println(result.toString());
//        this.printDocument(result);
//        out.println(childTagName + " value is : " + requestQueueName);
//        out.println(WebServiceRV.getInstance().getKanbanWorkId(testJobnumber));
    }

    protected String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }

    /**
     * Test of getMESUser method, of class WebServiceRV.
     */
//    @Test
//    public void getUser() {
//        out.println("getUser");
//        List<TestLineTypeUser> notExpResult = null;
//        User user = WebServiceRV.getInstance().getMESUser("A-7568");
//        assertNotEquals(notExpResult, user);
//        out.println(new Gson().toJson(user));
//    }
    public void printXml(List<String> l) {
        for (String st : l) {
            out.println(st);
        }
    }

    public void printDocument(Document doc) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }
}
