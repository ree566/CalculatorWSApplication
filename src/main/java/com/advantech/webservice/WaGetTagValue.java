/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webservice;

import com.advantech.service.db1.AlarmDOService;
import com.advantech.webservice.WaGetTagResponseModel.TagNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class WaGetTagValue extends WaTagValue {

    private static final Logger log = Logger.getLogger(WaGetTagValue.class.getName());

    private String urlGetTagValue;

    private static Map<String, Integer> map = new HashMap<>();

    @Autowired
    private AlarmDOService alarmDOService;

    public String getUrlGetTagValue() {
        return urlGetTagValue;
    }

    public void setUrlGetTagValue(String urlGetTagValue) {
        this.urlGetTagValue = urlGetTagValue;
    }

    public static Map<String, Integer> getMap() {
        return map;
    }

    public static void setMap(Map<String, Integer> map) {
        WaGetTagValue.map = map;
    }
    
    public void initActiveTagNodes() {
        List<String> allTagNames = alarmDOService.findCorrespondDOAll();
        String json = getJsonString(allTagNames);
        setTagToMap(getResponseBodys(json));
    }

    private WaGetTagResponseModel getResponseBodys(String json) {
        String jsonResponse = postJson(getUrlGetTagValue(), json);
        return jsonToObj(jsonResponse, WaGetTagResponseModel.class);
    }

    private Map<String, Integer> setTagToMap(WaGetTagResponseModel responseBodys) {
        map = responseBodys.getValues().stream()
                .filter(f -> (f.getValue() == 0 || f.getValue() == 1))
                .collect(Collectors.toMap(TagNode::getName, TagNode::getValue));

        log.log(Level.INFO, "initActiveTagNodes.map.size() : {0}", map.size());
        return map;
    }

    private String getJsonString(List<String> l) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"Tags\":[");
        for (String s : l) {
            sb.append("{\"Name\":\"");
            sb.append(s);
            sb.append("\"},");
        }
        sb.append("]}");
        return sb.toString();
    }
}
