package com.advantech.entity;
// Generated 2017/3/13 上午 09:50:39 by Hibernate Tools 4.3.1

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.HashSet;
import java.util.Set;

/**
 * Model generated by hbm2java
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Model implements java.io.Serializable {

    private int id;

    private String name;

    @JsonIgnore
    private Set sheetSpes = new HashSet(0);

    @JsonIgnore
    private Set sheetEes = new HashSet(0);

    @JsonIgnore
    private Set sheetIes = new HashSet(0);

    public Model() {
    }

    public Model(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Model(int id, String name, Set sheetSpes, Set sheetEes, Set sheetIes) {
        this.id = id;
        this.name = name;
        this.sheetSpes = sheetSpes;
        this.sheetEes = sheetEes;
        this.sheetIes = sheetIes;
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

    public Set getSheetSpes() {
        return this.sheetSpes;
    }

    public void setSheetSpes(Set sheetSpes) {
        this.sheetSpes = sheetSpes;
    }

    public Set getSheetEes() {
        return this.sheetEes;
    }

    public void setSheetEes(Set sheetEes) {
        this.sheetEes = sheetEes;
    }

    public Set getSheetIes() {
        return this.sheetIes;
    }

    public void setSheetIes(Set sheetIes) {
        this.sheetIes = sheetIes;
    }

}
