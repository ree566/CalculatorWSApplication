/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webservice;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class WaSetTagValue extends WaTagValue{

    private static final Logger log = Logger.getLogger(WaSetTagValue.class.getName());

    private String urlSetTagValue;
    
    private List<WaSetTagRequestModel> tagValues;

    public String getUrlSetTagValue() {
        return urlSetTagValue;
    }

    public void setUrlSetTagValue(String urlSetTagValue) {
        this.urlSetTagValue = urlSetTagValue;
    }

    public List<WaSetTagRequestModel> getTagValues() {
        return tagValues;
    }

    public void setTagValues(List<WaSetTagRequestModel> tagValues) {
        this.tagValues = tagValues;
    }

    // method.POST
    public void exchange(List<WaSetTagRequestModel> l) {
        setTagValues(l);
        String json = getJsonString();
        log.log(Level.INFO, "SetJsonString======={0}", json);
        postJson(urlSetTagValue,json);
    }

    private String getJsonString() {
        return "{\"Tags\":" + getJsonString(tagValues) + "}";
    }

}
