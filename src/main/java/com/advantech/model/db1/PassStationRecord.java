/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.advantech.webservice.XmlDateAdapter;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Wei.Cheng Cell passstation record
 */
@Entity
@Table(name = "PassStationRecord")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Table1")
public class PassStationRecord implements Serializable {

    private int id;

    @XmlElement(name = "BARCODE_NO")
    private String barcode;

    @XmlElement(name = "WIP_NO")
    private String po;

    @XmlElement(name = "LINE_DESC")
    private String lineName;

    @XmlElement(name = "LINE_ID")
    private Integer lineId;

    @XmlElement(name = "STATION_DESC")
    private String station;

    @XmlElement(name = "CREATE_DATE")
    @XmlJavaTypeAdapter(XmlDateAdapter.class)  
    private Date createDate;

    @XmlElement(name = "USER_NO")
    private String userNo;

    @XmlElement(name = "USER_NAME_CH")
    private String userName;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    @Size(min = 0, max = 20)
    @Column(name = "barcode", length = 20, nullable = false)
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @NotNull
    @Size(min = 0, max = 20)
    @Column(name = "po", length = 20, nullable = false)
    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    @NotNull
    @Size(min = 0, max = 20)
    @Column(name = "lineName", length = 20, nullable = false)
    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    @NotNull
    @Column(name = "lineId", nullable = false)
    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    @NotNull
    @Size(min = 0, max = 20)
    @Column(name = "station", length = 20, nullable = false)
    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createDate", length = 23, insertable = true, updatable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @NotNull
    @Size(min = 0, max = 20)
    @Column(name = "userNo", length = 20, nullable = false)
    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    @NotNull
    @Size(min = 0, max = 20)
    @Column(name = "userName", length = 20, nullable = false)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.barcode);
        hash = 89 * hash + Objects.hashCode(this.lineName);
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
        final PassStationRecord other = (PassStationRecord) obj;
        if (!Objects.equals(this.barcode, other.barcode)) {
            return false;
        }
        if (!Objects.equals(this.lineName, other.lineName)) {
            return false;
        }
        return true;
    }

}
