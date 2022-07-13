/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 首頁雙擊出現的選單可控制此專案的核心"Quartz的暫停與開始"(有風險，請將連結到此servlet的前端加強保護)
 */
package com.advantech.controller;

import com.advantech.facade.BabLineTypeFacade;
import com.advantech.facade.TestLineTypeFacade;
import com.advantech.helper.CronTrigMod;
import javax.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Wei.Cheng
 */
//@Controller
@RequestMapping(value = "/QuartzTriggerController")
public class QuartzTriggerController {

    private static final Logger log = LoggerFactory.getLogger(QuartzTriggerController.class);

    private BabLineTypeFacade babFacade;
    
    private TestLineTypeFacade testFacade;
    
    @RequestMapping(value = "/updateQuartz", method = {RequestMethod.POST})
    protected String updateQuartz(@RequestParam final String order, HttpServletRequest req) {
        String remoteIp = req.getRemoteAddr();
        
        switch(order){
            case "pause":
                babFacade.isNeedToOutput(false);
                testFacade.isNeedToOutput(false);
                log.info(remoteIp + " pause the lineTypeFacade job.");
                break;
            case "resume":
                babFacade.isNeedToOutput(true);
                testFacade.isNeedToOutput(true);
                log.info(remoteIp + " resume the lineTypeFacade job.");
            default:
                break;
        }

        return "success";
    }

    @RequestMapping(value = "/resetTestLineTypeQuartz", method = {RequestMethod.POST})
    protected String resetTestLineTypeQuartz(){
        return "success";
    }
}
