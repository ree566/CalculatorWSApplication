/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 卡組裝包裝個線別第一站保持"唯一"用
 */
package com.advantech.controller;

import com.advantech.datatable.DataTableResponse;
import static com.advantech.helper.SecurityPropertiesUtils.retrieveAndCheckUserInSession;
import com.advantech.model.db1.BabSensorLoginRecord;
import com.advantech.model.db1.BabSettingHistory;
import com.advantech.model.db1.Line;
import com.advantech.model.db1.TagNameComparison;
import com.advantech.model.db1.User;
import com.advantech.service.db1.BabSensorLoginRecordService;
import com.advantech.service.db1.BabSettingHistoryService;
import com.advantech.service.db1.LineService;
import com.advantech.service.db1.TagNameComparisonService;
import static com.google.common.base.Preconditions.*;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Wei.Cheng Because Line login and logout function are Deprecated Add
 * station login control to "decrease user Bab relative settings(bab people, bab
 * line...etc)"
 */
@Controller
@RequestMapping(value = "/BabSensorLoginController")
public class BabSensorLoginController {

    @Autowired
    private TagNameComparisonService tagNameComparisonService;

    @Autowired
    private BabSensorLoginRecordService babSensorLoginRecordService;

    @Autowired
    private BabSettingHistoryService babSettingHistoryService;

    @Autowired
    private LineService lineService;

    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @ResponseBody
    public String login(
            @RequestParam String tagName,
            @RequestParam String jobnumber
    ) {
        /* 
            ※The tagname input is already be decode from MD5
         */
        babSensorLoginRecordService.insert(tagName, jobnumber);
        return "success";
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.POST})
    @ResponseBody
    public String logout(
            @RequestParam String tagName,
            @RequestParam String jobnumber
    ) {
        BabSensorLoginRecord loginRecord = babSensorLoginRecordService.findBySensor(tagName);
        checkArgument(loginRecord != null, "Can't found login record in this sensor " + tagName);
        checkArgument(jobnumber.equals(loginRecord.getJobnumber()), "Record user is not matches " + jobnumber);
        babSensorLoginRecordService.delete(loginRecord);
        return "success";
    }

    @RequestMapping(value = "/checkStation", method = {RequestMethod.GET})
    @ResponseBody
    public boolean checkStation(
            @RequestParam String tagName,
            @RequestParam boolean isFirstStation
    ) {
        BabSettingHistory setting = babSettingHistoryService.findProcessingByTagName(tagName);
        if (setting == null) {
            return true;
        } else {
            if (isFirstStation) {
                checkArgument(setting.getStation() == 1,
                        "無法切換站別，此Sensor尚在上套的工單站別 " + setting.getStation() + " 進行中");
            } else {
                checkArgument(setting.getStation() != 1,
                        "無法切換站別，此Sensor尚在上套的工單站別 " + setting.getStation() + " 進行中");
            }
            return true;
        }
    }

    @RequestMapping(value = "/findSensorByEncode", method = {RequestMethod.GET})
    @ResponseBody
    public Object findSensorByEncode(@RequestParam() String encodeStr) {
        checkArgument(encodeStr.length() == 15, "The encodeStr length is not valid");
        TagNameComparison t = tagNameComparisonService.findByEncode(encodeStr);
        return t == null ? new Object() : t;
    }

    @RequestMapping(value = "/findAll", method = {RequestMethod.GET})
    @ResponseBody
    public DataTableResponse findAll(HttpServletRequest request) {
        List l;
        if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_OPER_IE")) {
            l = this.babSensorLoginRecordService.findAll();
        } else {
            Integer[] lines = findAvailableLine();
            l = this.babSensorLoginRecordService.findByLine(lines);
        }
        return new DataTableResponse(l);
    }

    @RequestMapping(value = "/findAvailableTagNameComparison", method = {RequestMethod.GET})
    @ResponseBody
    public List<TagNameComparison> findAvailableTagNameComparison(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN") || request.isUserInRole("ROLE_OPER_IE")) {
            return this.tagNameComparisonService.findAll();
        } else {
            Integer[] lines = findAvailableLine();
            List<TagNameComparison> availableTagName = this.tagNameComparisonService.findByLine(lines);
            return availableTagName;
        }
    }

    private Integer[] findAvailableLine() {
        User user = retrieveAndCheckUserInSession();
        List<Line> availableLine = lineService.findByUser(user);
        Integer[] lines = availableLine.stream()
                .map(l -> l.getId())
                .toArray(Integer[]::new);
        return lines;
    }
}
