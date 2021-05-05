/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 儲存資料到Bab用，負責第一站投工單
 */
package com.advantech.controller;

import com.advantech.endpoint.Endpoint6;
import com.advantech.helper.MailManager;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabSensorLoginRecord;
import com.advantech.model.db1.Line;
import com.advantech.model.db1.TagNameComparison;
import com.advantech.model.db1.User;
import com.advantech.service.db1.BabSensorLoginRecordService;
import com.advantech.service.db1.BabService;
import com.advantech.service.db1.PreAssyModuleTypeService;
import com.advantech.service.db1.TagNameComparisonService;
import com.advantech.service.db1.UserService;
import static com.google.common.base.Preconditions.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.mail.MessagingException;
import javax.validation.Valid;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Wei.Cheng
 */
@Controller
@RequestMapping(value = "/BabFirstStationController")
public class BabFirstStationController {

    private static final Logger log = LoggerFactory.getLogger(BabFirstStationController.class);

    private final String notify_name = "bab_run_in_alarm";

    @Autowired
    private BabService babService;

    @Autowired
    private TagNameComparisonService tagNameComparisonService;

    @Autowired
    private MailManager mailManager;

    @Autowired
    private UserService userService;

    @Autowired
    private BabSensorLoginRecordService babSensorLoginRecordService;

    @Autowired
    private PreAssyModuleTypeService moduleTypeService;

    @Autowired
    private Endpoint6 ep6;

    @RequestMapping(value = "/insert", method = {RequestMethod.POST})
    @ResponseBody
    protected String insert(
            @Valid @ModelAttribute Bab bab,
            @RequestParam String tagName,
            @RequestParam String jobnumber,
            @RequestParam(required = false) String recordLineType,
            @RequestParam(name = "moduleTypes[]", required = false) Integer[] moduleTypes
    ) {

        //Check人數
        checkArgument(!(bab.getIspre() != 1 && bab.getPeople() == 1), "非前置工單人數不可為1");

        if (bab.getIspre() == 1) {
            List modules = moduleTypeService.findByModelName(bab.getModelName());
            checkArgument(
                    (bab.getIspre() == 1 && !modules.isEmpty() && moduleTypes != null && moduleTypes.length > 0)
                    || (bab.getIspre() == 1 && modules.isEmpty() && (moduleTypes == null || moduleTypes.length == 0)),
                    "請至少選擇一項前置模組");

            if (moduleTypes != null && moduleTypes.length > 0) {
                //Check是否前置有填moduleType
                List typeList = moduleTypeService.findByPrimaryKeys(moduleTypes);
                Set typeSet = new HashSet(typeList);
                bab.setPreAssyModuleTypes(typeSet);
            }
        }

        //Check是否Login & Check jobnumber & tagName exist or not
        BabSensorLoginRecord loginRecord = babSensorLoginRecordService.findBySensor(tagName);
        checkArgument(loginRecord != null, "Can't find login record on this tagName");
        checkArgument(Objects.equals(loginRecord.getJobnumber(), jobnumber), "Jobnumber is not matches at loginRecord in database");

        TagNameComparison tag = tagNameComparisonService.findByLampSysTagName(tagName);
        checkArgument(tag != null, "Can't find tagName " + tagName + " in setting.");

        //Check station number is under line max people setting
        Line line = tag.getLine();
        checkArgument(bab.getPeople() + tag.getPosition() - 1 <= line.getPeople(),
                "People out of index(" + line.getPeople() + ")");

        bab.setLine(line);
        babService.checkAndInsert(bab, tag);

        //Don't show mail send error message when mail error caused.
//        try {
//            sendMailAfterBabRunIn(bab);
//        } catch (MessagingException e) {
//            log.error(e.getMessage());
//        }
        ep6.syncAndEcho();
        return "success";
    }

    private void sendMailAfterBabRunIn(Bab bab) throws MessagingException {

        List<User> l = userService.findByUserNotification(notify_name);

        String subject = "[藍燈系統]系統訊息";
        mailManager.sendMail(l, null, subject, generateMailBody(bab));

    }

    private String generateMailBody(Bab bab) {
        return new StringBuilder()
                .append("<p>現在時間 <strong>")
                .append(getToday())
                .append("</strong> </p>")
                .append("<p>系統開始測量線平衡與蒐集資料</p>")
                .append("<p>工單號碼: ")
                .append(bab.getPo())
                .append("</p><p>生產機種: ")
                .append(bab.getModelName())
                .append("</p><p>生產人數: ")
                .append(bab.getPeople())
                .append("</p><p>線別號碼: ")
                .append(bab.getLine().getName())
                .append("</p><p>詳細歷史資料請上 <a href='")
                .append("//172.20.131.52:8080/CalculatorWSApplication/BabTotal")
                .append("'>線平衡電子化系統</a> 中的歷史紀錄做查詢</p>")
                .toString();
    }

    private String getToday() {
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").print(new DateTime());
    }
}
