/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.model.db1;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Justin.Yeh
 */
@Entity
@Table(name = "Alarm_DO")
public class AlarmDO implements java.io.Serializable {

    private String processName;
    private String correspondDO;

    @Id
    @NotNull
    @Column(name = "Process_Name", length = 25, nullable = false)
    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    @NotNull
    @Column(name = "correspond_DO", length = 25, nullable = false)
    public String getCorrespondDO() {
        return correspondDO;
    }

    public void setCorrespondDO(String correspondDO) {
        this.correspondDO = correspondDO;
    }
}
