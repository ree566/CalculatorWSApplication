/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.LineType;
import com.advantech.model.db1.PrepareScheduleDailyRemark;
import java.util.List;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class PrepareScheduleDailyRemarkDAO extends AbstractDao<Integer, PrepareScheduleDailyRemark> implements BasicDAO_1<PrepareScheduleDailyRemark> {

    @Override
    public List<PrepareScheduleDailyRemark> findAll() {
        return super.createEntityCriteria().list();
    }

    @Override
    public PrepareScheduleDailyRemark findByPrimaryKey(Object obj_id) {
        return super.getByKey((int) obj_id);
    }
    
    public List<PrepareScheduleDailyRemark> findByLineTypeAndDate(List<LineType> lt, DateTime dt){
        return super.createEntityCriteria()
                .add(Restrictions.in("lineType", lt))
                .add(Restrictions.eq("date", dt.toDate()))
                .list();
    }

    @Override
    public int insert(PrepareScheduleDailyRemark pojo) {
        super.getSession().save(pojo);
        return 1;
    }

    @Override
    public int update(PrepareScheduleDailyRemark pojo) {
        super.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(PrepareScheduleDailyRemark pojo) {
        super.getSession().delete(pojo);
        return 1;
    }

}
