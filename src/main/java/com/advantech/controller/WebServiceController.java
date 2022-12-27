/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 顯示看板XML是否有資料用 與其他class無相依關係(開看板刷新時用，若無需求可刪除此servlet)
 */
package com.advantech.controller;

import com.advantech.webservice.Factory;
import com.advantech.webservice.WebServiceRV;
import javax.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Wei.Cheng
 */
@Controller
public class WebServiceController {

    @Autowired
    private WebServiceRV rv;

    //Check the webservice data is working or not(for testLineType)
    @RequestMapping(value = "/XMLServlet", method = {RequestMethod.GET}, produces = "text/xml; charset=UTF-8")
    @ResponseBody
    protected String testXml(HttpServletResponse res, final String factory) throws Exception {
        Factory f;
        switch (factory) {
            case "M6":
                f = Factory.TWM6;
                break;
            case "M2":
                f = Factory.TWM2;
                break;
            default:
                f = Factory.TWM3;
                break;
        }
        return rv.getKanbanUsersForString(f).get(1);
    }
}
