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
public class TestRecords implements Serializable, RvQueryResult<TestRecord>{

    @XmlElement(name = "Table1", type = TestRecord.class)
    private List<TestRecord> QryData;


    @Override
    public List<TestRecord> getQryData() {
        return QryData;
    }

    @Override
    public void setQryData(List<TestRecord> QryData) {
        this.QryData = QryData;
    }

}
