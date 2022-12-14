/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "TestPassStationDetail")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Table1")
public class TestPassStationDetail implements Serializable {

    private int id;

    @XmlElement(name = "BARCODE_NO")
    private String barcode;

    @XmlElement(name = "WIP_NO")
    private String po;

    @XmlElement(name = "ITEM_NO")
    private String modelName;

    @XmlElement(name = "STATION_NAME")
    private String station;

    @XmlElement(name = "RULE_STATUS")
    private String state;

    @XmlElement(name = "USER_NO")
    private String jobnumber;

    @XmlElement(name = "USER_NAME_CH")
    private String username;

    @XmlElement(name = "CREATE_DATE")
    private Date createDate;

    @XmlElement(name = "STATION_FLAG")
    private int stationFlag;

    @XmlElement(name = "LINE_DESC")
    private String lineName;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "barcode", nullable = false)
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Column(name = "po", nullable = false)
    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    @Column(name = "modelName", nullable = false)
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Column(name = "station", nullable = false)
    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    @Column(name = "[state]", nullable = false)
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "jobnumber", nullable = false)
    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    @Column(name = "user_name", nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createDate", length = 23, nullable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "station_flag", nullable = false)
    public int getStationFlag() {
        return stationFlag;
    }

    public void setStationFlag(int stationFlag) {
        this.stationFlag = stationFlag;
    }

    @Column(name = "line_name", nullable = false)
    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.barcode);
        hash = 41 * hash + Objects.hashCode(this.po);
        hash = 41 * hash + Objects.hashCode(this.modelName);
        hash = 41 * hash + Objects.hashCode(this.station);
        hash = 41 * hash + Objects.hashCode(this.jobnumber);
        hash = 41 * hash + this.stationFlag;
        hash = 41 * hash + Objects.hashCode(this.lineName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestPassStationDetail other = (TestPassStationDetail) obj;
        if (this.stationFlag != other.stationFlag) {
            return false;
        }
        if (!Objects.equals(this.barcode, other.barcode)) {
            return false;
        }
        if (!Objects.equals(this.po, other.po)) {
            return false;
        }
        if (!Objects.equals(this.modelName, other.modelName)) {
            return false;
        }
        if (!Objects.equals(this.station, other.station)) {
            return false;
        }
        if (!Objects.equals(this.jobnumber, other.jobnumber)) {
            return false;
        }
        if (!Objects.equals(this.lineName, other.lineName)) {
            return false;
        }
        return true;
    }

}
