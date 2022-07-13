/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.BabStandardTimeHistory;
import java.util.List;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class BabStandardTimeHistoryDAO extends AbstractDao<Integer, BabStandardTimeHistory> implements BasicDAO_1<BabStandardTimeHistory> {

    @Override
    public List<BabStandardTimeHistory> findAll() {
        return super.createEntityCriteria().list();
    }

    @Override
    public BabStandardTimeHistory findByPrimaryKey(Object obj_id) {
        return super.getByKey((int) obj_id);
    }
    
    public BabStandardTimeHistory findByBab(int babId){
        return (BabStandardTimeHistory) super.createEntityCriteria()
                .add(Restrictions.eq("bab.id", babId))
                .uniqueResult();
    }

    @Override
    public int insert(BabStandardTimeHistory pojo) {
        this.getSession().save(pojo);
        return 1;
    }

    @Override
    public int update(BabStandardTimeHistory pojo) {
        this.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(BabStandardTimeHistory pojo) {
        this.getSession().delete(pojo);
        return 1;
    }

}
