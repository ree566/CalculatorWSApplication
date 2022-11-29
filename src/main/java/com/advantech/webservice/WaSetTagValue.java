/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.webservice;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Justin.Yeh
 */
@Component
public class WaSetTagValue extends WaTagValue {

    private static final Logger log = Logger.getLogger(WaSetTagValue.class.getName());

    private String urlSetTagValue;

    public String getUrlSetTagValue() {
        return urlSetTagValue;
    }

    public void setUrlSetTagValue(String urlSetTagValue) {
        this.urlSetTagValue = urlSetTagValue;
    }

    public void exchange(List<WaSetTagRequestModel> l) {
        String json = String.format("{\"Tags\":%s}", super.getJsonString(l));
        log.log(Level.INFO, "SetJsonString======={0}", json);
        postJson(urlSetTagValue, json);
    }
}
