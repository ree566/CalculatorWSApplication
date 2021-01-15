/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.helper.DatetimeGenerator;
import com.advantech.helper.MailManager;
import com.advantech.helper.PropertiesReader;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabAlarmHistory;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Line;
import com.advantech.model.db1.User;
import com.advantech.service.db1.BabService;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.UserService;
import static com.google.common.base.Preconditions.checkState;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Component
@Transactional
public class CountermeasureAlarm {

    private static final Logger log = LoggerFactory.getLogger(CountermeasureAlarm.class);
    private final DecimalFormat formatter = new DecimalFormat("#.##%");
    private final DatetimeGenerator dg = new DatetimeGenerator("yyyy-MM-dd HH:mm");

    @Autowired
    private BabService babService;

    @Autowired
    private UserService userService;

    @Autowired
    private FloorService floorService;

    @Autowired
    private MailManager mailManager;

    private final String notificationName = "abnormal_unfill_alarm";
    private final String subject = "[藍燈系統]未填寫異常回覆工單列表 ";

    @Autowired
    private PropertiesReader p;

    public void execute() throws Exception {
        try {
            //Link the mailloop to send daily mail
            sendMail();
        } catch (Exception ex) {
            log.error(ex.toString());
        }
    }

    public void sendMail() throws Exception {
        List<User> ccMailLoop = userService.findByUserNotificationAndNotLineOwner(notificationName);
        String[] ccMailUsers = ccMailLoop.stream().map(u -> u.getUsername()).toArray(String[]::new);
        List<Floor> floors = floorService.findAll();
        for (Floor f : floors) {
            List<User> mailLoop = userService.findLineOwnerBySitefloor(f.getId());
            if (!mailLoop.isEmpty()) {
                String[] mailUsers = mailLoop.stream().map(u -> u.getUsername()).toArray(String[]::new);
                log.info("Begin send countermeasure alarm to sitefloor " + f.getName());
                log.info("Line owners " + Arrays.toString(mailUsers));
                log.info("CC users " + Arrays.toString(ccMailUsers));

                // when user sitefloor is not setting, turn user's mail to mail cc loop
//                String mailBody = this.generateMailBody(f.getId());
//                if (!"".equals(mailBody)) {
//                    //有資料再寄信
//                    mailManager.sendMail(mailLoop, ccMailLoop, subject + f.getName() + "F", mailBody);
//                }
            }
        }
    }

    public String generateMailBody(int floor_id) {
        DateTime today = new DateTime();

        List<Bab> l = babService.findUnReplyed(
                floor_id,
                today.minusDays(p.getBabCountermeasureAlertDay()).withHourOfDay(0),
                today.withHourOfDay(23)
        );
        if (l.isEmpty()) {
            return "";
        } else {
            Map<String, Set<User>> highlightLines = new HashMap();
            StringBuilder sb = new StringBuilder();
            sb.append("<style>table {border-collapse: collapse;} table, th, td {border: 1px solid black; padding: 5px;}</style>");
            sb.append("<p>Dear 使用者:</p>");
            sb.append("<p>以下是亮燈頻率高於基準值，尚未回覆異常原因的工單列表</p>");
            sb.append("<p>請於 <mark><strong style='color: red'>今日下班前</strong></mark> 至 藍燈系統 > 線平衡資料查詢頁面 > 檢視詳細 填寫工單異常因素，謝謝</p>");
            sb.append("<table>");
            sb.append("<tr><th>製程</th><th>線別</th><th>工單</th><th>機種</th><th>亮燈頻率</th><th>數量</th><th>投入時間</th></tr>");
            l.forEach((b) -> {
                Line line = b.getLine();
                String lineName = line.getName();
                Set<User> users = line.getUsers();
                Set<BabAlarmHistory> alarmHistorys = b.getBabAlarmHistorys();
                BabAlarmHistory alarmHistory = alarmHistorys.stream().findFirst().orElse(null);
                checkState(alarmHistory != null, "Can't find alarmHistory on bab id " + b.getId());
                sb.append("<tr><td>")
                        .append(line.getLineType().getName())
                        .append("</td><td>")
                        .append(line.getName())
                        .append("</td><td>")
                        .append(b.getPo())
                        .append("</td><td>")
                        .append(b.getModelName())
                        .append("</td><td>")
                        .append(formatter.format((alarmHistory.getFailPcs() * 1.0) / alarmHistory.getTotalPcs()))
                        .append("</td><td>")
                        .append(alarmHistory.getTotalPcs())
                        .append("</td><td>")
                        .append(dg.dateFormatToString(b.getBeginTime()))
                        .append("</td></tr>");
                if (!highlightLines.containsKey(lineName)) {
                    highlightLines.put(lineName, line.getUsers());
                }
            });
            sb.append("</table>");
            sb.append("<p>線別負責人: </p>");

            highlightLines.keySet().stream().map((line) -> {
                Set<User> set = highlightLines.get(line);
                sb.append("<p>");
                sb.append(line);
                sb.append(" : ");
                if (set.isEmpty()) {
                    sb.append("unknown");
                } else {
                    set.forEach((user) -> {
                        sb.append(user.getUsernameCh());
                        sb.append("、");
                    });
                }
                return line;
            }).forEachOrdered((_item) -> {
                sb.append("</p>");
            });

            sb.append("<p>資料共計: ");
            sb.append(l.size());
            sb.append(" 筆</p>");
            return sb.toString();
        }

    }

}
