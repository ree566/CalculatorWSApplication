/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model.db1;

import com.advantech.webservice.mes.RvQueryResult;
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
@XmlRootElement(name = "NewDataSet")
public class UsersInfoOnMes implements Serializable, RvQueryResult<UserInfoOnMes> {

    @XmlElement(name = "Table1", type = UserInfoOnMes.class)
    private List<UserInfoOnMes> QryData;

    @Override
    public List<UserInfoOnMes> getQryData() {
        return QryData;
    }

    @Override
    public void setQryData(List<UserInfoOnMes> QryData) {
        this.QryData = QryData;
    }

}
