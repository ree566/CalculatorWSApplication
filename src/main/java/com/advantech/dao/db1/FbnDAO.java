/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.Fbn;
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class FbnDAO extends AbstractDao<Integer, Fbn> {

    public List<Fbn> findToday() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("YY/MM/dd");
        return super.createEntityCriteria()
                .add(Restrictions.eq("LogDate", dtf.print(DateTime.now())))
                .list();
    }

    public Fbn getLastInputData() {
        DetachedCriteria maxId = DetachedCriteria.forClass(Fbn.class)
                .setProjection(Projections.max("id"));
        return (Fbn) super.createEntityCriteria()
                .add(Property.forName("id").eq(maxId))
                .uniqueResult();
    }

    //利用檢視表(過濾後FBN資料表資訊)得到當前sensor時間 websocket用 
    public List<Map> getSensorCurrentStatus() {
        return super.getSession()
                .createSQLQuery("SELECT * FROM {h-schema}vw_SensorStatusPerStationToday")
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    //用途同上method comment
    public List<Map> getBarcodeCurrentStatus() {
        return super.getSession()
                .createSQLQuery("SELECT * FROM {h-schema}vw_BarcodeStatusPerStationToday")
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Fbn> findByTagNameAndDate(String tagName, DateTime sD, DateTime eD) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yy/MM/dd");
        DateTimeFormatter fmt2 = DateTimeFormat.forPattern("HH:mm:ss");

        return super.createEntityCriteria()
                .add(Restrictions.eq("tagName", tagName))
                .add(Restrictions.eq("logDate", fmt.print(sD)))
                .add(Restrictions.between("logTime", fmt2.print(sD), fmt2.print(eD)))
                .list();
    }

}
