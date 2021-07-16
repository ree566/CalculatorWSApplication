/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.Fbn;
import com.advantech.dao.db1.FbnDAO;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class FbnService {

    @Autowired
    private FbnDAO fbnDAO;

    public Fbn getLastInputData() {
        return fbnDAO.getLastInputData();
    }

    public List<Fbn> getSensorDataInDay() {
        return fbnDAO.findToday();
    }

    public List<Map> getSensorCurrentStatus() {
        return fbnDAO.getSensorCurrentStatus();
    }

    public List<Map> getBarcodeCurrentStatus() {
        return fbnDAO.getBarcodeCurrentStatus();
    }

    public Integer checkLastFBNMinuteDiff() {
        DateTime now = DateTime.now();
        Fbn f = this.getLastInputData();
        return f == null ? null : Minutes.minutesBetween(convert(getSensorTime(f)), now).getMinutes();
    }

    public Integer checkHoursDiff(Fbn f) {
        DateTime now = DateTime.now();
        return f == null ? null : Hours.hoursBetween(convert(getSensorTime(f)), now).getHours();
    }

    private String getSensorTime(Fbn f) {
        return convertFullDateTime(f.getLogDate(), f.getLogTime());
    }

    private String convertFullDateTime(String... str) {
        String dateString = "";
        for (String st : str) {
            dateString += st.trim() + " ";
        }
        return dateString;
    }

    private DateTime convert(String date) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yy/MM/dd HH:mm:ss ");
        return dtf.parseDateTime(date);
    }

    public List<Fbn> findByTagNameAndDate(String tagName, DateTime sD, DateTime eD) {
        return fbnDAO.findByTagNameAndDate(tagName, sD, eD);
    }

}
