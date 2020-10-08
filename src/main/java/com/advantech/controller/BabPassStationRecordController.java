/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.controller;

import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabPassStationRecord;
import com.advantech.model.db1.BabSettingHistory;
import com.advantech.service.db1.BabPassStationRecordService;
import com.advantech.service.db1.BabSettingHistoryService;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Wei.Cheng
 */
@Controller
@RequestMapping(value = "/BabPassStationRecordController")
public class BabPassStationRecordController {

    @Autowired
    private BabPassStationRecordService babPassStationRecordService;

    @Autowired
    private BabSettingHistoryService settingHistoryService;

    @RequestMapping(value = "/redirect", method = {RequestMethod.GET})
    protected ModelAndView findBabAndredirect(
            @CookieValue(required = true) String userInfo,
            @RequestParam String tagName
    ) {
        BabSettingHistory setting = settingHistoryService.findProcessingByTagName(tagName);
        checkArgument(setting != null, "Can't find processing bab");
        ModelAndView mav = new ModelAndView("barcode_input_click");
        mav.addObject("tagName", tagName);
        mav.addObject("bab_id", setting.getBab().getId());
        return mav;
    }

    @RequestMapping(value = "/findLastProcessByTagName", method = {RequestMethod.GET})
    @ResponseBody
    protected BabPassStationRecord findLastProcessByTagName(
            @CookieValue(required = true) String userInfo,
            @RequestParam String tagName
    ) {
        return babPassStationRecordService.findLastProcessingByTagName(tagName);
    }

    @RequestMapping(value = "/insert", method = {RequestMethod.POST})
    @ResponseBody
    protected int insert(
            @CookieValue(required = true) String userInfo,
            @RequestParam String barcode,
            @RequestParam String tagName,
            @ModelAttribute Bab bab
    ) {
        int count = babPassStationRecordService.checkStationInfoAndInsert(bab, tagName, barcode);
        return count;
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
    @ResponseBody
    protected String delete(@RequestParam String barcode, @ModelAttribute Bab bab) {
        return "success";
    }

    @RequestMapping(value = {"/findLastInput"}, method = {RequestMethod.GET})
    @ResponseBody
    protected BabPassStationRecord findLastInput(@ModelAttribute Bab bab) {
        List<BabPassStationRecord> l = this.babPassStationRecordService.findByBab(bab);
        return l.isEmpty() ? new BabPassStationRecord() : l.get(l.size() - 1);
    }
}
