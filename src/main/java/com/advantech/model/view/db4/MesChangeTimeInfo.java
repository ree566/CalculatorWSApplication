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
public class MesChangeTimeInfo implements Serializable {

    private String WIP_NO;
    private String UNIT_NO;
    private String ITEM_NO;
    private BigDecimal CHANGE_TIME = new BigDecimal(0);
    private BigDecimal STANDARD_CHANGE_TIME = new BigDecimal(0);
    private BigDecimal POWER_CNT = new BigDecimal(0);
    private String LINE_DESC;

    public MesChangeTimeInfo() {
    }

    public String getWIP_NO() {
        return WIP_NO;
    }

    public void setWIP_NO(String WIP_NO) {
        this.WIP_NO = WIP_NO;
    }

    public String getUNIT_NO() {
        return UNIT_NO;
    }

    public void setUNIT_NO(String UNIT_NO) {
        this.UNIT_NO = UNIT_NO;
    }

    public String getITEM_NO() {
        return ITEM_NO;
    }

    public void setITEM_NO(String ITEM_NO) {
        this.ITEM_NO = ITEM_NO;
    }

    public BigDecimal getCHANGE_TIME() {
        return CHANGE_TIME;
    }

    public void setCHANGE_TIME(BigDecimal CHANGE_TIME) {
        this.CHANGE_TIME = CHANGE_TIME;
    }

    public BigDecimal getSTANDARD_CHANGE_TIME() {
        return STANDARD_CHANGE_TIME;
    }

    public void setSTANDARD_CHANGE_TIME(BigDecimal STANDARD_CHANGE_TIME) {
        this.STANDARD_CHANGE_TIME = STANDARD_CHANGE_TIME;
    }

    public BigDecimal getPOWER_CNT() {
        return POWER_CNT;
    }

    public void setPOWER_CNT(BigDecimal POWER_CNT) {
        this.POWER_CNT = POWER_CNT;
    }

    public String getLINE_DESC() {
        return LINE_DESC;
    }

    public void setLINE_DESC(String LINE_DESC) {
        this.LINE_DESC = LINE_DESC;
    }

}
