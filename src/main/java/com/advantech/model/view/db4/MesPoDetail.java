/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.view.db4;

import java.io.Serializable;

/**
 *
 * @author MFG.ESOP
 */
public class MesPoDetail implements Serializable {

    public String WIP_NO;
    public String LINE_DESC;

    public String getWIP_NO() {
        return WIP_NO;
    }

    public void setWIP_NO(String WIP_NO) {
        this.WIP_NO = WIP_NO;
    }

    public String getLINE_DESC() {
        return LINE_DESC;
    }

    public void setLINE_DESC(String LINE_DESC) {
        this.LINE_DESC = LINE_DESC;
    }

}
