/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import com.advantech.model.db1.Bab;
import com.advantech.service.db1.BabService;
import com.advantech.webservice.Factory;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestController {

    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Before()  //这个方法在每个方法执行之前都会执行一遍
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).apply(springSecurity()).build();  //初始化MockMvc对象
    }

    private void printResult(String controller_method_url, RequestMethod method, Map<String, String> param) throws Exception {
        MockHttpServletRequestBuilder requestBuilder;

        switch (method) {
            case GET:
                requestBuilder = get(controller_method_url);
                break;
            case POST:
                requestBuilder = post(controller_method_url);
                break;
            default:
                throw new UnsupportedOperationException("Method not support");
        }

        requestBuilder.contentType(MediaType.APPLICATION_FORM_URLENCODED);
        param.forEach((k, v) -> {
            requestBuilder.param(k, v);
        });

        String responseString = mockMvc.perform(requestBuilder.with(user("sysop")))
                .andExpect(status().isOk()) //返回的状态是200
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json = " + responseString);
    }

    @Test
    public void testBabChartController() throws Exception {
        //findLineBalanceDetail
        Map<String, String> param1 = ImmutableMap.<String, String>builder()
                .put("id", "62032")
                .build();

        printResult("/BabChartController/findLineBalanceDetail", RequestMethod.GET, param1);

        //getSensorDiffChart
        printResult("/BabChartController/getSensorDiffChart", RequestMethod.GET, param1);
    }

    @Test
    public void testBabController() throws Exception {
        //findByMultipleClause
        Map<String, String> param1 = ImmutableMap.<String, String>builder()
                .put("lineType_id", "-1")
                .put("sitefloor_id", "-1")
                .put("startDate", "2020-02-05")
                .put("endDate", "2020-02-07")
                .put("aboveStandard", "true")
                .build();
        printResult("/BabController/findByMultipleClause", RequestMethod.GET, param1);

        //findProcessingByTagName
        Map<String, String> param2 = ImmutableMap.<String, String>builder()
                .put("tagName", "NA-S-1")
                .build();
        printResult("/BabController/findProcessingByTagName", RequestMethod.GET, param2);

        //findByModelAndDate
        Map<String, String> param3 = ImmutableMap.<String, String>builder()
                .put("modelName", "")
                .put("startDate", "2020-02-05")
                .put("endDate", "2020-02-07")
                .build();
        printResult("/BabController/findByModelAndDate", RequestMethod.GET, param3);

        //findAllModelName
        Map<String, String> param4 = ImmutableMap.<String, String>builder()
                .build();
        printResult("/BabController/findAllModelName", RequestMethod.GET, param4);

        //findBabTimeGapPerLine
        Map<String, String> param5 = ImmutableMap.<String, String>builder()
                .put("startDate", "2020-02-05")
                .put("endDate", "2020-02-07")
                .build();
        printResult("/BabController/findBabTimeGapPerLine", RequestMethod.GET, param5);
    }

    @Autowired
    private BabService babService;

    @Test
    @Transactional
    @Rollback(true)
    public void testBabFirstStationController() throws Exception {
        //findBabTimeGapPerLine
        Map<String, String> param = ImmutableMap.<String, String>builder()
                .put("po", "TEST")
                .put("modelName", "TEST")
                .put("people", "3")
                .put("ispre", "0")
                .put("tagName", "NA-S-1")
                .put("jobnumber", "A-7777")
                .build();
        printResult("/BabFirstStationController/insert", RequestMethod.POST, param);

        DateTime sD = new DateTime().withTime(0, 0, 0, 0);
        DateTime eD = new DateTime().withTime(23, 0, 0, 0);
        List<Bab> l = babService.findByModelAndDate("TEST", sD, eD);
        assertTrue(!l.isEmpty());
        babService.delete(l.get(l.size()));
    }

    @Test
    public void testBabLineController() throws Exception {
        //findAll
        Map<String, String> param1 = ImmutableMap.<String, String>builder()
                .put("sitefloor", "5")
                .build();
        printResult("/BabLineController/findAll", RequestMethod.GET, param1);
        
        //findWithLineType
        Map<String, String> param2 = ImmutableMap.<String, String>builder()
                .build();
        printResult("/BabLineController/findWithLineType", RequestMethod.GET, param2);
        
        //findLineType
        Map<String, String> param3 = ImmutableMap.<String, String>builder()
                .build();
        printResult("/BabLineController/findLineType", RequestMethod.GET, param3);
        
        //findByUser
        Map<String, String> param4 = ImmutableMap.<String, String>builder()
                .build();
        printResult("/BabLineController/findByUser", RequestMethod.GET, param4);
        
        //findBySitefloorAndLineType
        Map<String, String> param5 = ImmutableMap.<String, String>builder()
                .put("floorName", "5")
                .put("lineType_id[]", "1,3")
                .build();
        printResult("/BabLineController/findBySitefloorAndLineType", RequestMethod.GET, param5);
    }

    @Test
    public void testBabLineProductivityExcludeController() throws Exception {
        //insert
        Map<String, String> param1 = ImmutableMap.<String, String>builder()
                .put("id[]", "1")
                .build();
        printResult("/BabLineProductivityExcludeController/insert", RequestMethod.POST, param1);
    }

//-------------------Other controller testing part-------------------    
//    @Test
    public void testM3Model() throws Exception {
        assertEquals("UPOS540FP1801-T", getModel("PSI6484ZA", Factory.TWM3)); //Test M3 model
    }

//    @Test
    public void testM2Model() throws Exception {
        assertEquals("APAX-5580-433AE", getModel("FII6245ZA", Factory.TWM2)); //Test M2 model
    }

//    @Test
    public void testM6Model() throws Exception {
        assertEquals("IDK-C15G-IEXGA2E", getModel("AEI6068RQ", Factory.TWM6)); //Test M6 model
    }

    private String getModel(String po, Factory f) throws Exception {
        String responseString = mockMvc.perform(
                get("/ModelController/findModelNameByPoAndFactory").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("po", po)
                        .param("factory", f.toString())
        ) //返回的状态是200
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json = " + responseString);
        return responseString;
//        .andExpect(status().isOk())
    }

//    @Test
    public void testArrayParamInput() throws Exception {
        String responseString = mockMvc.perform(
                get("/BabLineProductivityExcludeController/insert").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id[]", "1,2,3,4,5,6")
        ) //返回的状态是200
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json = " + responseString);
    }

//    @Test
    public void testCloseBab() throws Exception {
        String responseString = mockMvc.perform(
                post("/BabOtherStationController/stationComplete").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("bab_id", "33428")
                        .param("tagName", "L3-S-3")
                        .param("jobnumber", "A-8236")
                        .param("pcs", "1,1,1")
        ) //返回的状态是200
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("--------返回的json = " + responseString);
    }
}
