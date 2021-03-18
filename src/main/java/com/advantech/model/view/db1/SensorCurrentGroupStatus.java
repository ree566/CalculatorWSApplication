/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.view.db1;

import java.io.Serializable;

/**
 *
 * @author Wei.Cheng
 */
public class SensorCurrentGroupStatus implements Serializable {

    private int id;
    private String TagName;
    private String LogDate;
    private String LogTime;
    private int LogMilliSecond;
    private int LogValue;
    private int groupid;
    private Integer isused;
    private Integer diff;
    private Integer bab_id;
    private Integer station;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagName() {
        return TagName;
    }

    public void setTagName(String TagName) {
        this.TagName = TagName;
    }

    public String getLogDate() {
        return LogDate;
    }

    public void setLogDate(String LogDate) {
        this.LogDate = LogDate;
    }

    public String getLogTime() {
        return LogTime;
    }

    public void setLogTime(String LogTime) {
        this.LogTime = LogTime;
    }

    public int getLogMilliSecond() {
        return LogMilliSecond;
    }

    public void setLogMilliSecond(int LogMilliSecond) {
        this.LogMilliSecond = LogMilliSecond;
    }

    public int getLogValue() {
        return LogValue;
    }

    public void setLogValue(int LogValue) {
        this.LogValue = LogValue;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public Integer getIsused() {
        return isused;
    }

    public void setIsused(Integer isused) {
        this.isused = isused;
    }

    public Integer getDiff() {
        return diff;
    }

    public void setDiff(Integer diff) {
        this.diff = diff;
    }

    public Integer getBab_id() {
        return bab_id;
    }

    public void setBab_id(Integer bab_id) {
        this.bab_id = bab_id;
    }

    public Integer getStation() {
        return station;
    }

    public void setStation(Integer station) {
        this.station = station;
    }

}
