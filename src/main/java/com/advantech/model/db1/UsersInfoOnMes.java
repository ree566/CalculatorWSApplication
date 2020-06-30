/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Wei.Cheng
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "root")
public class UsersInfoOnMes implements Serializable{

    @XmlElement(name = "QryUserInfo001", type = UserInfoOnMes.class)
    private List<UserInfoOnMes> QryData;


    public List<UserInfoOnMes> getQryData() {
        return QryData;
    }

    public void setQryData(List<UserInfoOnMes> QryData) {
        this.QryData = QryData;
    }

}
