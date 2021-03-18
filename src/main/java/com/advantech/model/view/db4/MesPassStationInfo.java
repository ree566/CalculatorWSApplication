/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.view.db4;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Wei.Cheng
 */
public class MesPassStationInfo implements Serializable {

    private String WIP_NO;
    private BigDecimal PLAN_QTY = new BigDecimal(0);
    private String ITEM_NO;
    private BigDecimal PASS_QTY = new BigDecimal(0);
    private BigDecimal TOTAL_PASS_QTY = new BigDecimal(0);
    private String UNIT_NO;
    private BigDecimal STATION_ID = new BigDecimal(0);

    public MesPassStationInfo() {
    }

    public MesPassStationInfo(String WIP_NO, String ITEM_NO, String UNIT_NO) {
        this.WIP_NO = WIP_NO;
        this.ITEM_NO = ITEM_NO;
        this.UNIT_NO = UNIT_NO;
    }

    public String getWIP_NO() {
        return WIP_NO;
    }

    public void setWIP_NO(String WIP_NO) {
        this.WIP_NO = WIP_NO;
    }

    public BigDecimal getPLAN_QTY() {
        return PLAN_QTY;
    }

    public void setPLAN_QTY(BigDecimal PLAN_QTY) {
        this.PLAN_QTY = PLAN_QTY;
    }

    public String getITEM_NO() {
        return ITEM_NO;
    }

    public void setITEM_NO(String ITEM_NO) {
        this.ITEM_NO = ITEM_NO;
    }

    public BigDecimal getPASS_QTY() {
        return PASS_QTY;
    }

    public void setPASS_QTY(BigDecimal PASS_QTY) {
        this.PASS_QTY = PASS_QTY;
    }

    public BigDecimal getTOTAL_PASS_QTY() {
        return TOTAL_PASS_QTY;
    }

    public void setTOTAL_PASS_QTY(BigDecimal TOTAL_PASS_QTY) {
        this.TOTAL_PASS_QTY = TOTAL_PASS_QTY;
    }

    public String getUNIT_NO() {
        return UNIT_NO;
    }

    public void setUNIT_NO(String UNIT_NO) {
        this.UNIT_NO = UNIT_NO;
    }

    public BigDecimal getSTATION_ID() {
        return STATION_ID;
    }

    public void setSTATION_ID(BigDecimal STATION_ID) {
        this.STATION_ID = STATION_ID;
    }

}
