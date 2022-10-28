/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.controller;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author Wei.Cheng
 */
@Controller
//@RequestMapping("/")
public class IndexController {

    @Autowired
    private MessageSource messageSource;

    /*--作業人員使用頁面--*/
    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) String locale) {
        setLanguage(model, request, response, locale);
        return "/home";
    }

    @RequestMapping("/Bab")
    public String bab(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "babpage";
    }

    @RequestMapping("/Test")
    public String test(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "testpage";
    }
    
    @RequestMapping("/Cell")
    public String cell(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "cell";
    }
    
    @RequestMapping("/Error")
    public String error(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "temp/error";
    }
    
    @RequestMapping("/pages/admin/TestTotal")
    public String testTotal(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/testTotal1";
    }
    
    @RequestMapping("/pages/admin/TestTotalDetail")
    public String testTotalDetail(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/testTotal";
    }
    
    @RequestMapping("/pages/admin/BabTotal")
    public String babTotal(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "/pages/admin/babTotal_1";
    }
    
    @RequestMapping("/pages/admin/Sensor")
    public String sensordata(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "/pages/admin/sensor";
    }
    
    @RequestMapping("/pages/admin/Barcode")
    public String barcodedata(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/barcode";
    }
    
    @RequestMapping("/SysInfo")
    public String sysInfoOldRedirect(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "redirect:/pages/admin/SysInfo";
    }
    
    @RequestMapping("/pages/admin/SysInfo")
    public String sysInfo(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/sysInfo";
    }
    
    @RequestMapping("/pages/admin/SensorAdjust")
    public String sensorAdjust(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/sensorAdjust";
    }
    
    @RequestMapping("/pages/admin/BarcodeAdjust")
    public String barcodeAdjust(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/barcodeAdjust";
    }
    
    @RequestMapping("/pages/admin/TotalMap")
    public String totalMap(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/totalMap";
    }
    
    @RequestMapping("/pages/admin/BabDetailInfo")
    public String babDetailInfo(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/babDetailInfo";
    }
    
    @RequestMapping("/pages/admin/BabDetailInfo2")
    public String babDetailInfo2(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/babDetailInfo_1";
    }
    
    @RequestMapping("/pages/admin/BabLineProductivity")
    public String babLineProductivity(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/babLineProductivity";
    }
    
    @RequestMapping("/pages/admin/FqcDashBoard")
    public String fqcDashBoard(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/fqcDashBoard";
    }
    
    @RequestMapping("/pages/admin/FqcModelStandardTime")
    public String fqcModelStandardTime(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/fqcModelStandardTime";
    }
    
    @RequestMapping("/pages/admin/FqcRecord")
    public String fqcRecord(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/fqcRecord";
    }
    
    @RequestMapping("/pages/admin/ModelSopRemark")
    public String modelSopRemark(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/modelSopRemark";
    }
    
    @RequestMapping("/pages/admin/AssyDelete")
    public String assyDelete(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/assyDelete";
    }
    
    @RequestMapping("/pages/admin/BabPassStationRecord")
    public String babPassStationRecord(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/babPassStationRecord";
    }
    
    @RequestMapping("/pages/admin/BabPassStationExceptionReport")
    public String babPassStationExceptionReport(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/babPassStationExceptionReport";
    }
    
    @RequestMapping("/pages/admin/BabPreAssyProductivity")
    public String babPreAssyProductivity(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/babPreAssyProductivity";
    }
    
    @RequestMapping("/pages/admin/SensorTest")
    public String sensorTest(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/sensorTest";
    }
    
    @RequestMapping("/pages/admin/PreAssyModuleStandardTime")
    public String preAssyModuleStandardTime(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/preAssyModuleStandardTime";
    }
    
    @RequestMapping("/pages/admin/BabPreAssyDetail")
    public String babPreAssyDetail(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/babPreAssyDetail";
    }
    
    @RequestMapping("/pages/admin/ChangePassword")
    public String changePassword(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/changePassword";
    }
    
    @RequestMapping("/pages/admin/PreAssyPercentage")
    public String PreAssyPercentage(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        setLanguage(model, request, response, locale);
        return "pages/admin/preAssyPercentage";
    }

    private void setLanguage(Model model, HttpServletRequest request, HttpServletResponse response, String locale) {
        if (locale != null && !"".equals(locale)) {
            // 分割参数
            String[] languages = locale.split("_");

            // 设置系统语言
            setLanguage(model, request, response, new Locale(languages[0], languages[1]));
        }
    }

    /**
     * 设定语言格式
     */
    private void setLanguage(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) {
        // 获取LocaleResolver
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

        // 设置Locale信息
        localeResolver.setLocale(request, response, locale);

        // 传递正确的Locale信息到页面
        model.addAttribute("encoding", messageSource.getMessage("encoding", new Object[0], locale));
    }
}
