/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webservice;

import com.advantech.service.db1.AlarmDOService;
import com.advantech.webservice.WaGetTagResponseModel.TagNode;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    private WaGetTagResponseModel body;

    @Autowired
    private AlarmDOService alarmDOService;

    public String getUrlGetTagValue() {
        return urlGetTagValue;
    }

    public void setUrlGetTagValue(String urlGetTagValue) {
        this.urlGetTagValue = urlGetTagValue;
    }

    public WaGetTagResponseModel getBody() {
        return body;
    }

    public void setBody(WaGetTagResponseModel body) {
        this.body = body;
    }

    public void initActiveTagNodes() {
        List<String> allTagNames = alarmDOService.findcorrespondDOAll();
        String json = getJsonString(allTagNames);
        getResponseBodys(json);
        if (body != null) {
            setTagToMap(body);
        }
    }

    private void getResponseBodys(String json) {
        String jsonResponse = postJson(getUrlGetTagValue(), json);
        setBody(jsonToObj(jsonResponse, WaGetTagResponseModel.class));
    }

    private Map<String, Integer> setTagToMap(WaGetTagResponseModel responseBodys) {
        for (TagNode item : responseBodys.getValues()) {
            Integer value = item.getValue();
            if (value == 1 || value == 0) {
                map.put(item.getName(), value);
            }
        }
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
