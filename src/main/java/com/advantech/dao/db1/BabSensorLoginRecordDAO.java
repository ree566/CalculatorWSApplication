/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.BabSensorLoginRecord;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class BabSensorLoginRecordDAO extends AbstractDao<Integer, BabSensorLoginRecord> implements BasicDAO_1<BabSensorLoginRecord> {

    @Override
    public List<BabSensorLoginRecord> findAll() {
        return super.createEntityCriteria().list();
    }

    @Override
    public BabSensorLoginRecord findByPrimaryKey(Object obj_id) {
        return super.getByKey((Integer) obj_id);
    }
    
    public BabSensorLoginRecord findBySensor(String tagName){
        Criteria c = super.createEntityCriteria();
        c.add(Restrictions.eq("tagName.name", tagName));
        return (BabSensorLoginRecord) c.uniqueResult();
    }
    
    public List<BabSensorLoginRecord> findByLine(Integer... line_id){
        Criteria c = super.createEntityCriteria();
        c.createAlias("tagName", "tagName");
        c.createAlias("tagName.tagNameComparisons", "tagNameComparison");
        c.createAlias("tagNameComparison.line", "line");
        c.add(Restrictions.in("line.id", line_id));
        return c.list();
    }
    
    public List<BabSensorLoginRecord> findByJobnumber(String jobnumber){
        Criteria c = super.createEntityCriteria();
        c.add(Restrictions.eq("jobnumber", jobnumber));
        return c.list();
    }

    @Override
    public int insert(BabSensorLoginRecord pojo) {
        this.getSession().save(pojo);
        return 1;
    }

    @Override
    public int update(BabSensorLoginRecord pojo) {
        this.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(BabSensorLoginRecord pojo) {
        this.getSession().delete(pojo);
        return 1;
    }

}
