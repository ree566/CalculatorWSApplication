package com.advantech.model;
// Generated 2017/4/7 下午 02:26:06 by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * WorktimeColumnGroup generated by hbm2java
 */
@Entity
@Table(name = "Worktime_ColumnGroup",
         schema = "dbo",
         catalog = "E_Document"
)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class WorktimeColumnGroup implements java.io.Serializable {

    private int id;
    private Unit unit;
    private String columnName;

    public WorktimeColumnGroup() {
    }

    public WorktimeColumnGroup(int id) {
        this.id = id;
    }

    public WorktimeColumnGroup(int id, Unit unit, String columnName) {
        this.id = id;
        this.unit = unit;
        this.columnName = columnName;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    public Unit getUnit() {
        return this.unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Lob
    @Column(name = "column_name")
    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

}
