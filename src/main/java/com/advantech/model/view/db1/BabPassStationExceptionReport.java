/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.view.db1;

import java.io.Serializable;

/**
 *
 * @author Wei.Cheng vw_UserInfoRemote
 */
public class BabPassStationExceptionReport implements Serializable {

    private int line_id;
    private String line_name;
    private int data_lost_cnt;
    private int zero_time_cnt;
    private int short_time_cnt;
    private int pcs_sum;
    private int total_qty;

    public int getLine_id() {
        return line_id;
    }

    public void setLine_id(int line_id) {
        this.line_id = line_id;
    }

    public String getLine_name() {
        return line_name;
    }

    public void setLine_name(String line_name) {
        this.line_name = line_name;
    }

    public int getData_lost_cnt() {
        return data_lost_cnt;
    }

    public void setData_lost_cnt(int data_lost_cnt) {
        this.data_lost_cnt = data_lost_cnt;
    }

    public int getZero_time_cnt() {
        return zero_time_cnt;
    }

    public void setZero_time_cnt(int zero_time_cnt) {
        this.zero_time_cnt = zero_time_cnt;
    }

    public int getShort_time_cnt() {
        return short_time_cnt;
    }

    public void setShort_time_cnt(int short_time_cnt) {
        this.short_time_cnt = short_time_cnt;
    }

    public int getPcs_sum() {
        return pcs_sum;
    }

    public void setPcs_sum(int pcs_sum) {
        this.pcs_sum = pcs_sum;
    }

    public int getTotal_qty() {
        return total_qty;
    }

    public void setTotal_qty(int total_qty) {
        this.total_qty = total_qty;
    }

}
