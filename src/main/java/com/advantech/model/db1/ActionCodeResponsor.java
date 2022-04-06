package com.advantech.model.db1;
// Generated 2017/11/13 下午 01:41:36 by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ActionCodeMapping generated by hbm2java
 */
@Entity
@Table(name = "ActionCodeResponsor")
public class ActionCodeResponsor implements java.io.Serializable {

    private int id;
    private ActionCode actionCode;
    private Unit unit;
    private Floor floor;
    private String userName;

    public ActionCodeResponsor() {
    }

    public ActionCodeResponsor(ActionCode actionCode, Unit unit, Floor floor, String userName) {
        this.actionCode = actionCode;
        this.unit = unit;
        this.floor = floor;
        this.userName = userName;
    }

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ac_id")
    public ActionCode getActionCode() {
        return this.actionCode;
    }

    public void setActionCode(ActionCode actionCode) {
        this.actionCode = actionCode;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    public Unit getUnit() {
        return this.unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    public Floor getFloor() {
        return this.floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    @Column(name = "[user_name]", length = 20)
    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}