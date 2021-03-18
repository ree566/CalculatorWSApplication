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
 * tbfn_BabAvg(:bab_id)
 */
public class BabAvg implements Serializable {

    private int bab_id;
    private int station;
    private Double average;

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

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

}
