/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 每兩個小時幫忙把BAB資料做儲存
 */
package com.advantech.quartzJob;

import com.advantech.model.db1.Bab;
import com.advantech.helper.ApplicationContextHelper;
import com.advantech.helper.MailManager;
import com.advantech.model.db1.User;
import com.advantech.model.view.db1.SensorCurrentGroupStatus;
import com.advantech.service.db1.SqlProcedureService;
import com.advantech.service.db1.SqlViewService;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author Wei.Cheng
 */
public class CheckSensor extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(CheckSensor.class);

    private Bab bab;
    private final DateTimeFormatter dtf = DateTimeFormat.forPattern("yy/MM/dd HH:mm:ss ");
    private final int excludeHour = 12;

    //These param will inject by class SensorDetect
    private int expireTime;
    private int detectPeriod;
    private List<User> responsors;
    private List<User> ccLoops;

    private final SqlProcedureService procViewService;

    private MailManager mailManager;

    public CheckSensor() {
        procViewService = (SqlProcedureService) ApplicationContextHelper.getBean("sqlProcedureService");
        mailManager = (MailManager) ApplicationContextHelper.getBean("mailManager");
    }

    @Override
    public void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        try {
            checkSensorAndSendMail();
        } catch (MessagingException ex) {
            log.error(ex.toString());
        }
    }

    //定時查看sensor資料是否又暫停or異常
    private void checkSensorAndSendMail() throws MessagingException {

        if (responsors.isEmpty()) {
            return;
        }

        List<SensorCurrentGroupStatus> sensorStatus = procViewService.findSensorCurrentGroupStatus(bab.getId());
        DateTime currentTime = new DateTime();

        int period;

        //所有13點開始，且13:30以前的感應器都去看與12:00相差多久，避開中午休息時間
        if (currentTime.getHourOfDay() == 13 && currentTime.getMinuteOfHour() < 30) {
            period = new Period(new DateTime(bab.getBeginTime()), new DateTime().withTime(12, 00, 0, 0)).toStandardMinutes().getMinutes();
        } else {
            period = periodToNow(new DateTime(bab.getBeginTime()));
        }

        //開線後N分鐘開始監聽
        if (period >= expireTime && currentTime.getHourOfDay() != excludeHour) {
            if (bab.getPeople() != sensorStatus.size()) {
                //看是第幾站沒有資料，回報
                Integer lostStation = sensorStatus.size() + 1;
                sendMail(bab.getLine().getName(), "訊號遺失於站別 " + lostStation);
            } else {
                //看看時間距離現在多久了，超過N秒沒動作，回報

                //按照ID排序，取最大值(最後更新時間)
                Collections.sort(
                        sensorStatus,
                        (final SensorCurrentGroupStatus object1, final SensorCurrentGroupStatus object2)
                        -> Integer.compare(object1.getId(), object2.getId())
                );

                SensorCurrentGroupStatus lastStatus = sensorStatus.get(sensorStatus.size() - 1);
                String date = lastStatus.getLogDate();
                String time = lastStatus.getLogTime();

                int periodTime = periodToNow(date + time);
                if (isExpire(periodTime)) {
                    sendMail(bab.getLine().getName(), "Sensor已經超過 " + periodTime + " 分鐘沒有動作，最後一次感應在TagName " + lastStatus.getTagName());
                }
            }
        }
    }

    private int periodToNow(DateTime time) {
        DateTime currentTime = new DateTime();
        return Minutes.minutesBetween(time, currentTime).getMinutes();
    }

    private int periodToNow(String time) {
        DateTime checkedTime = dtf.parseDateTime(time);
        return periodToNow(checkedTime);
    }

    private boolean isExpire(int time) {
        return time > expireTime;
    }

    private void sendMail(String tagName, String message) throws MessagingException {
        if (responsors != null && !responsors.isEmpty()) {
            String subject = "[藍燈系統]Sensor異常訊息";
            String mailBody = generateMailBody(tagName, message);
            mailManager.sendMail(responsors, ccLoops, subject, mailBody);
        }
    }

    private String generateMailBody(String tagName, String message) {
        return new StringBuilder()
                .append("<p>時間 <strong>")
                .append(new Date())
                .append(" Sensor異常訊息如下</strong></p>")
                .append("<p style='color:red'>")
                .append(tagName)
                .append(" ")
                .append(message)
                .append("</p>")
                .append("<p>請協助確認感應器是否正常，謝謝。</p>")
                .append("<p>詳情請至系統目錄/CalculatorWSApplication/pages/admin/SysInfo後台頁面上方的感應器頁籤查看該線別狀態</p>")
                .append("<p>本系統將再 ")
                .append(detectPeriod)
                .append(" 分鐘之後再次確認此線別感應器狀態。</p>")
                .toString();
    }

    public void setBab(Bab bab) {
        this.bab = bab;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public void setDetectPeriod(int detectPeriod) {
        this.detectPeriod = detectPeriod;
    }

    public void setResponsors(List<User> responsors) {
        this.responsors = responsors;
    }

    public void setCcLoops(List<User> ccLoops) {
        this.ccLoops = ccLoops;
    }

}
