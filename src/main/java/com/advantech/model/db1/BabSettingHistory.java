/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "BabSettingHistory")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BabSettingHistory implements Serializable {

    private int id;
    private Bab bab;
    private int station;
    private SensorTransform tagName;
    private String jobnumber;
    private Date createTime;
    private Date lastUpdateTime;

//    @JsonIgnore
    private Set<BabPreAssyPcsRecord> babPreAssyPcsRecords = new HashSet<BabPreAssyPcsRecord>(0);

    public BabSettingHistory() {
    }

    public BabSettingHistory(Bab bab, int station, SensorTransform tagName, String jobnumber) {
        this.bab = bab;
        this.station = station;
        this.tagName = tagName;
        this.jobnumber = jobnumber;
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
    @JoinColumn(name = "bab_id", nullable = false)
    public Bab getBab() {
        return bab;
    }

    public void setBab(Bab bab) {
        this.bab = bab;
    }

    @Column(name = "station", nullable = false)
    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tagName", nullable = false)
    public SensorTransform getTagName() {
        return tagName;
    }

    public void setTagName(SensorTransform tagName) {
        this.tagName = tagName;
    }

    @Column(name = "jobnumber", length = 50, nullable = false)
    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "btime", length = 23, updatable = false)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lastUpdateTime", length = 23)
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "babSettingHistory")
    public Set<BabPreAssyPcsRecord> getBabPreAssyPcsRecords() {
        return babPreAssyPcsRecords;
    }

    public void setBabPreAssyPcsRecords(Set<BabPreAssyPcsRecord> babPreAssyPcsRecords) {
        this.babPreAssyPcsRecords = babPreAssyPcsRecords;
    }

}
