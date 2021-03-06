/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.advantech.converter.FactoryConverter;
import com.advantech.converter.LineStatusConverter;
import com.advantech.webservice.Factory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
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

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "FqcLine")
public class FqcLine implements Serializable {

    private int id;
    private String name;
    private Floor floor;
    private LineStatus lineStatus;
    private int lock;
    private Factory factory;

    @JsonIgnore
    private Set<FqcLoginRecord> fqcLoginRecords = new HashSet<FqcLoginRecord>(0);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    @Column(name = "isused")
    @Convert(converter = LineStatusConverter.class)
    public LineStatus getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(LineStatus lineStatus) {
        this.lineStatus = lineStatus;
    }

    @Column(name = "lock", nullable = false)
    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fqcLine")
    public Set<FqcLoginRecord> getFqcLoginRecords() {
        return fqcLoginRecords;
    }

    public void setFqcLoginRecords(Set<FqcLoginRecord> fqcLoginRecords) {
        this.fqcLoginRecords = fqcLoginRecords;
    }

    @Column(name = "factory")
    @Convert(converter = FactoryConverter.class)
    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

}
