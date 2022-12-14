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
public class TestPassStationDetails implements Serializable, RvQueryResult<TestPassStationDetail>{

    @XmlElement(name = "Table1", type = TestPassStationDetail.class)
    private List<TestPassStationDetail> QryData;


    @Override
    public List<TestPassStationDetail> getQryData() {
        return QryData;
    }

    @Override
    public void setQryData(List<TestPassStationDetail> QryData) {
        this.QryData = QryData;
    }

}
