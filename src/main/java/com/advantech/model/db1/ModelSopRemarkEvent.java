/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "ModelSopRemarkEvent")
public class ModelSopRemarkEvent implements Serializable {

    private int id;
    private ModelSopRemark modelSopRemark;
    private User user;
    private String action;
    private Date lastUpdateTime;

    public ModelSopRemarkEvent() {
    }

    public ModelSopRemarkEvent(ModelSopRemark modelSopRemark, User user, String action) {
        this.modelSopRemark = modelSopRemark;
        this.user = user;
        this.action = action;
    }

    @Id
    @GeneratedValue
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "[user_id]")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "[action]", nullable = false, length = 10)
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdateTime", length = 23, updatable = false)
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

}
