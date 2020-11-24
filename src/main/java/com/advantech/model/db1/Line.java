/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "LS_Line")
public class Line implements Serializable {

    private int id;
    private String name;
    private int lock;
    private int people;
    private LineType lineType;
    private Floor floor;

    @JsonIgnore
    private Set<User> users = new HashSet<User>(0);

    @JsonIgnore
    private Set<Bab> babs = new HashSet<Bab>(0);

    @JsonIgnore
    private Set<ModelSopRemark> modelSopRemarks = new HashSet<ModelSopRemark>(0);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "[name]", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "people", nullable = false)
    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lineType_id")
    public LineType getLineType() {
        return lineType;
    }

    public void setLineType(LineType lineType) {
        this.lineType = lineType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    @Column(name = "lock", nullable = false)
    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Line_User_REF", joinColumns = {
        @JoinColumn(name = "line_id", nullable = false, updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "[user_id]", nullable = false, updatable = false)})
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "line")
    public Set<Bab> getBabs() {
        return babs;
    }

    public void setBabs(Set<Bab> babs) {
        this.babs = babs;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ModelSopRemark_Line_REF", joinColumns = {
        @JoinColumn(name = "line_id", nullable = false, insertable = false, updatable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "modelSopRemark_id", nullable = false, insertable = false, updatable = false)})
    public Set<ModelSopRemark> getModelSopRemarks() {
        return modelSopRemarks;
    }

    public void setModelSopRemarks(Set<ModelSopRemark> modelSopRemarks) {
        this.modelSopRemarks = modelSopRemarks;
    }

}
