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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Wei.Cheng
 */
@Entity
@Table(name = "Sensor")
public class Sensor implements Serializable {

    private String name;

    @JsonIgnore
    private Set<TagNameComparison> tagNameComparisons = new HashSet<>(0);

    @Id
    @Column(name = "[name]", unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "id.orginTagName", cascade = CascadeType.ALL)
    public Set<TagNameComparison> getTagNameComparisons() {
        return tagNameComparisons;
    }

    public void setTagNameComparisons(Set<TagNameComparison> tagNameComparisons) {
        this.tagNameComparisons = tagNameComparisons;
    }

}
