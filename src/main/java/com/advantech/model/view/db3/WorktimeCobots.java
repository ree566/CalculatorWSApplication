/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.model.view.db3;

import java.io.Serializable;

/**
 *
 * @author Wei.Cheng
 */
public class WorktimeCobots implements Serializable {

    private String modelName;
    private String cobots;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getCobots() {
        return cobots;
    }

    public void setCobots(String cobots) {
        this.cobots = cobots;
    }

}
