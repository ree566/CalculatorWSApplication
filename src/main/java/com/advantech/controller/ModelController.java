/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 排定抓取資料的排程
 */
package com.advantech.controller;

import com.advantech.converter.FactoryControllerConverter;
import com.advantech.webservice.Factory;
import com.advantech.webservice.WebServiceRV;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Wei.Cheng 前端給予PO, servlet 負責 sched the new polling job.
 */
@Controller
@RequestMapping("/ModelController")
public class ModelController {

    private static final Logger log = LoggerFactory.getLogger(ModelController.class);

    @Autowired
    private WebServiceRV rv;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(Factory.class, new FactoryControllerConverter());
    }

    //Default find M3's modelName when user not present factory name
    @RequestMapping(value = "/findModelNameByPo", method = {RequestMethod.GET})
    @ResponseBody
    protected String findModelNameByPo(@RequestParam String po) {
        return this.findModelName(po, Factory.TWM3);
    }

    @RequestMapping(value = "/findModelNameByPoAndFactory", method = {RequestMethod.GET})
    @ResponseBody
    protected String findModelNameByPo(@RequestParam String po, @RequestParam Factory factory) {
        return this.findModelName(po, factory);
    }

    private String findModelName(String po, Factory f) {
        String modelname = rv.getModelNameByPo(po, f);
        return (modelname == null ? "data not found" : convertString(modelname));
    }

    private String convertString(String input) {
        StringBuilder converstring = new StringBuilder();
        Pattern p = Pattern.compile("[\\w|-]");
        Matcher matcher = p.matcher(input);
        while (matcher.find()) {
            converstring.append(matcher.group());
        }
        return converstring.toString();
    }
}
