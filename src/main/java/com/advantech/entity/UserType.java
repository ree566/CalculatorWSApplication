package com.advantech.entity;
// Generated 2017/3/13 上午 09:50:39 by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.HashSet;
import java.util.Set;

/**
 * UserType generated by hbm2java
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class UserType implements java.io.Serializable {

    private int id;

    private String name;

    @JsonIgnore
    private Set identits = new HashSet(0);

    public UserType() {
    }

    public UserType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserType(int id, String name, Set identits) {
        this.id = id;
        this.name = name;
        this.identits = identits;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set getIdentits() {
        return this.identits;
    }

    public void setIdentits(Set identits) {
        this.identits = identits;
    }

}
