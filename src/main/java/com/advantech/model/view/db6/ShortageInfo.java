/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.view.db6;

import java.io.Serializable;

/**
 *
 * @author Wei.Cheng
 */
public class ShortageInfo implements Serializable {

    private String po;
    private String material;
    private Integer shortageCnt;

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Integer getShortageCnt() {
        return shortageCnt;
    }

    public void setShortageCnt(Integer shortageCnt) {
        this.shortageCnt = shortageCnt;
    }

}
