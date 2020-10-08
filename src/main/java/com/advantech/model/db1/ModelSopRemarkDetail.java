/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "ModelSopRemarkDetail")
public class ModelSopRemarkDetail implements Serializable {

    private int id;
    private ModelSopRemark modelSopRemark;
    private int station;
    private String sopName;
    private String sopPage;
    private Integer standardTime;
    private String remark;

    public ModelSopRemarkDetail() {
    }

    public ModelSopRemarkDetail(ModelSopRemark modelSopRemark, int station, String sopName, String sopPage) {
        this.modelSopRemark = modelSopRemark;
        this.station = station;
        this.sopName = sopName;
        this.sopPage = sopPage;
    }

    public ModelSopRemarkDetail(ModelSopRemark modelSopRemark, int station, String sopName, String sopPage, Integer standardTime, String remark) {
        this.modelSopRemark = modelSopRemark;
        this.station = station;
        this.sopName = sopName;
        this.sopPage = sopPage;
        this.standardTime = standardTime;
        this.remark = remark;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modelSopRemark_id")
    public ModelSopRemark getModelSopRemark() {
        return modelSopRemark;
    }

    public void setModelSopRemark(ModelSopRemark modelSopRemark) {
        this.modelSopRemark = modelSopRemark;
    }

    @Column(name = "station", nullable = false)
    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    @Column(name = "sop_name", nullable = false, length = 50)
    public String getSopName() {
        return sopName;
    }

    public void setSopName(String sopName) {
        this.sopName = sopName;
    }

    @Column(name = "sop_page", nullable = false, length = 50)
    public String getSopPage() {
        return sopPage;
    }

    public void setSopPage(String sopPage) {
        this.sopPage = sopPage;
    }

    @Column(name = "standardTime")
    public Integer getStandardTime() {
        return standardTime;
    }

    public void setStandardTime(Integer standardTime) {
        this.standardTime = standardTime;
    }

    @Column(name = "remark", length = 150)
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
