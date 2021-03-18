/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.view.db1;

import com.advantech.model.db1.Bab;
import java.util.List;
import org.joda.time.Interval;

/**
 *
 * @author Wei.Cheng
 */
public class BabProcessDetail {

    private int lineId;
    private String dateString;
    private List<Bab> babs;
    private List<Interval> intervals;
    private int totalGapsTimeInDay;

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public List<Bab> getBabs() {
        return babs;
    }

    public void setBabs(List<Bab> babs) {
        this.babs = babs;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Interval> intervals) {
        this.intervals = intervals;
    }

    public int getTotalGapsTimeInDay() {
        return totalGapsTimeInDay;
    }

    public void setTotalGapsTimeInDay(int totalGapsTimeInDay) {
        this.totalGapsTimeInDay = totalGapsTimeInDay;
    }

}
