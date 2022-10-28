/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.controller;

import com.advantech.datatable.DataTableResponse;
import com.advantech.model.db1.Bab;
import com.advantech.service.db1.BabService;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Wei.Cheng
 */
@Controller
@RequestMapping(value = "/BabController")
public class BabController {

    @Autowired
    private BabService babService;

    @RequestMapping(value = "/findByMultipleClause", method = {RequestMethod.GET})
    @ResponseBody
    protected List<Bab> findByMultipleClause(
            @RequestParam int lineType_id,
            @RequestParam int sitefloor_id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate,
            @RequestParam boolean aboveStandard) {
        return babService.findByMultipleClause(startDate, endDate, lineType_id, sitefloor_id, aboveStandard);
    }

    @RequestMapping(value = "/findProcessingByTagName", method = {RequestMethod.GET})
    @ResponseBody
    protected List<Map> findProcessingByTagName(@RequestParam String tagName) {
        return babService.findProcessingByTagName(tagName);
    }

    @RequestMapping(value = "/findByModelAndDate", method = {RequestMethod.GET})
    @ResponseBody
    public DataTableResponse findByModelAndDate(
            @RequestParam String modelName,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate) {
        return new DataTableResponse(babService.findByModelAndDate(modelName, startDate, endDate));
    }

    @RequestMapping(value = "/findAllModelName", method = {RequestMethod.GET})
    @ResponseBody
    public List<String> findAllModelName() {
        return babService.findAllModelName();
    }

    @RequestMapping(value = "/findBabTimeGapPerLine", method = {RequestMethod.GET})
    @ResponseBody
    public DataTableResponse findBabTimeGapPerLine(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate) {
        return new DataTableResponse(babService.findBabTimeGapPerLine(startDate, endDate));
    }
    
    @RequestMapping(value = "/findByPreAssyModuleType", method = {RequestMethod.GET})
    @ResponseBody
    public DataTableResponse findByPreAssyModuleType(
            @RequestParam int preAssyModuleTypeId, @RequestParam String po) {
        
        return new DataTableResponse(babService.findByPreAssyModuleType(preAssyModuleTypeId, po));
        
    }
    
    
    
}
