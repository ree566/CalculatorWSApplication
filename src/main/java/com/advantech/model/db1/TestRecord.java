/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.advantech.converter.ReplyStatusConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "TestLineTypeRecord")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Table1")
public class TestRecord implements Serializable {

    private int id;

    @XmlElement(name = "USER_NO")
    private String userId;

    @XmlElement(name = "USER_NAME")
    private String userName;

    @XmlElement(name = "PRODUCTIVITY")
    private Double productivity;

    private TestTable testTable;

    private Date lastUpdateTime;
    
    private ReplyStatus replyStatus = ReplyStatus.NO_NEED_TO_REPLY;
    
    @JsonIgnore
    private Set<TestRecordRemark> testRecordRemarks = new HashSet<TestRecordRemark>(0);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "[user_id]", nullable = false, length = 20)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "[user_name]", nullable = false, length = 20)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "productivity", nullable = false, precision = 10, scale = 2)
    public Double getProductivity() {
        return productivity;
    }

    public void setProductivity(Double productivity) {
        this.productivity = productivity;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    public TestTable getTestTable() {
        return testTable;
    }

    public void setTestTable(TestTable testTable) {
        this.testTable = testTable;
    }

    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd'T'kk:mm:ss.SSS'Z'", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "saveTime", length = 23, updatable = false)
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    @NotNull
    @Column(name = "replyFlag")
    @Convert(converter = ReplyStatusConverter.class)
    public ReplyStatus getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(ReplyStatus replyStatus) {
        this.replyStatus = replyStatus;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "testRecord")
    public Set<TestRecordRemark> getTestRecordRemarks() {
        return testRecordRemarks;
    }

    public void setTestRecordRemarks(Set<TestRecordRemark> testRecordRemarks) {
        this.testRecordRemarks = testRecordRemarks;
    }
    
    

}
