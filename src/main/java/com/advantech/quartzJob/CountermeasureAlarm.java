/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.helper.ApplicationContextHelper;
import com.advantech.helper.DatetimeGenerator;
import com.advantech.helper.MailSend;
import com.advantech.helper.StringParser;
import com.advantech.model.Floor;
import com.advantech.model.User;
import com.advantech.service.FloorService;
import com.advantech.service.SystemReportService;
import com.advantech.service.UserService;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author Wei.Cheng
 */
public class CountermeasureAlarm extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(CountermeasureAlarm.class);
    private final DecimalFormat formatter = new DecimalFormat("#.##%");
    private final DatetimeGenerator dg = new DatetimeGenerator("yyyy-MM-dd HH:mm");

    private final SystemReportService systemReportService;
    private final UserService userService;
    private final FloorService floorService;

    private final String notificationName = "abnormal_unfill_alarm";
    private final String subject = "[藍燈系統]未填寫異常回覆工單列表 ";

    public CountermeasureAlarm() {
        systemReportService = (SystemReportService) ApplicationContextHelper.getBean("systemReportService");
        userService = (UserService) ApplicationContextHelper.getBean("userService");
        floorService = (FloorService) ApplicationContextHelper.getBean("floorService");
    }

    @Override
    public void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        try {
            //Link the mailloop to send daily mail
            sendMail();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }

    public void sendMail() throws Exception {
        JSONArray ccMailLoop = conbineUsers(userService.findByUserNotificationAndNotLineOwner(notificationName));
        List<Floor> floors = floorService.findAll();
        for (Floor f : floors) {
            JSONArray mailLoop = conbineUsers(userService.findLineOwnerBySitefloor(f.getId()));
            // when user sitefloor is not setting, turn user's mail to mail cc loop
            
                String mailBody = this.generateMailBody(f.getId());
                if (!"".equals(mailBody)) { //有資料再寄信
                    MailSend.getInstance().sendMail(mailLoop, ccMailLoop, subject + f.getName() + "F", mailBody);
                }
            
        }
    }

    public String generateMailBody(int floor_id) {
        List<Map> l = systemReportService.getUnFillCountermeasureBabs(floor_id);
        if (l.isEmpty()) {
            return "";
        } else {
            Map<String, Set<String>> highlightLines = new HashMap();
            StringBuilder sb = new StringBuilder();
            sb.append("<style>table {border-collapse: collapse;} table, th, td {border: 1px solid black; padding: 5px;}</style>");
            sb.append("<p>Dear 使用者:</p>");
            sb.append("<p>以下是亮燈頻率高於基準值，尚未回覆異常原因的工單列表</p>");
            sb.append("<p>請於 <mark><strong style='color: red'>今日下班前</strong></mark> 至 藍燈系統 > 線平衡資料查詢頁面 > 檢視詳細 填寫工單異常因素，謝謝</p>");
            sb.append("<table>");
            sb.append("<tr><th>製程</th><th>線別</th><th>工單</th><th>機種</th><th>亮燈頻率</th><th>數量</th><th>投入時間</th></tr>");
            for (Map m : l) {
                String lineName = (String) m.get("lineName"), userName = StringParser.clobToString((Clob) m.get("user_name"));
                sb.append("<tr><td>")
                        .append(m.get("linetype"))
                        .append("</td><td>")
                        .append(m.get("lineName"))
                        .append("</td><td>")
                        .append(m.get("PO"))
                        .append("</td><td>")
                        .append(m.get("Model_name"))
                        .append("</td><td>")
                        .append(formatter.format(m.get("almPercent")))
                        .append("</td><td>")
                        .append(m.get("qty"))
                        .append("</td><td>")
                        .append(dg.dateFormatToString(m.get("Btime")))
                        .append("</td></tr>");
                if (highlightLines.containsKey(lineName)) {
                    highlightLines.get(lineName).add(userName);
                } else {
                    Set set = new HashSet();
                    set.add(userName);
                    highlightLines.put(lineName, set);
                }
            }
            sb.append("</table>");
            sb.append("<p>線別負責人: </p>");

            for (String line : highlightLines.keySet()) {
                Set<String> set = highlightLines.get(line);
                sb.append("<p>");
                sb.append(line);
                sb.append(" : ");
                for (String user : set) {
                    sb.append(user);
                }
                sb.append("</p>");
            }

            sb.append("<p>資料共計: ");
            sb.append(l.size());
            sb.append(" 筆</p>");
            return sb.toString();
        }

    }

    private JSONArray conbineUsers(List<User> l) {
        JSONArray arr = new JSONArray();
        for (User u : l) {
            arr.put(u.getUsername());
        }
        return arr;
    }

}
