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
 * usp_GetLastGroupStatus
 */
public class BabLastGroupStatus implements Serializable {

    private int bab_id;
    private int station;
    private int groupid;
    private int diff;
    private String tagName;
    private boolean ismax = false;

    public int getBab_id() {
        return bab_id;
    }

    public void setBab_id(int bab_id) {
        this.bab_id = bab_id;
    }

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getDiff() {
        return diff;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean getIsmax() {
        return ismax;
    }

    public void setIsmax(boolean ismax) {
        this.ismax = ismax;
    }

}
