/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao;

import com.advantech.model.BabPcsDetailHistory;
import com.advantech.model.BabStatus;
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class BabPcsDetailHistoryDAO extends AbstractDao<Integer, BabPcsDetailHistory> {

    public List<BabPcsDetailHistory> findByBab(int bab_id) {
        return super.createEntityCriteria()
                .add(Restrictions.eq("bab.id", bab_id))
                .list();
    }

    /*Use for chart generate*/
    public List<Map> findByBabForMap(int bab_id) {
        return super.getSession().createQuery(
                "select new Map(id as id, bab.id as bab_id, tagName.name as tagName, "
                + "station as station, groupid as groupid, diff as diff, "
                + "lastUpdateTime as lastUpdateTime) "
                + "from BabPcsDetailHistory where bab.id = :bab_id order by groupid, station, tagName")
                .setParameter("bab_id", bab_id)
                .list();
    }

    public List<BabPcsDetailHistory> findWithBabAndAlarmDetails(String modelName, String lineType, DateTime sD, DateTime eD) {
        return super.createEntityCriteria()
                .createAlias("bab", "b")
                .createAlias("b.line", "l")
                .createAlias("l.lineType", "lt")
                .add(Restrictions.eq("b.modelName", modelName))
                .add(Restrictions.eq("lt.name", lineType))
                .add(Restrictions.eq("b.babStatus", BabStatus.CLOSED))
                .setMaxResults(10)
                .list();
    }

}
