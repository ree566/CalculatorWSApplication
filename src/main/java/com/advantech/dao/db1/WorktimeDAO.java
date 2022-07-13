/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import static com.advantech.helper.HibernateBatchUtils.flushIfReachFetchSize;
import com.advantech.model.db1.Worktime;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class WorktimeDAO extends AbstractDao<String, Worktime> implements BasicDAO_1<Worktime> {

    @Override
    public List<Worktime> findAll() {
        return super.createEntityCriteria().list();
    }

    @Override
    public Worktime findByPrimaryKey(Object obj_id) {
        return super.getByKey((String) obj_id);
    }

    public Worktime findByModelName(String modelName) {
        return (Worktime) super.createEntityCriteria()
                .add(Restrictions.eq("modelName", modelName))
                .uniqueResult();
    }

    public List<Worktime> findNotZeroPackingLeadTime() {
        return super.createEntityCriteria()
                .add(Restrictions.ne("packingLeadTime", BigDecimal.ZERO))
                .list();
    }

    @Override
    public int insert(Worktime pojo) {
        this.getSession().save(pojo);
        return 1;
    }

    public int insert(List<Worktime> l) {
        Session session = super.getSession();
        int currentRow = 1;
        for (Worktime a : l) {
            session.save(a);
            flushIfReachFetchSize(session, currentRow++);
        }
        return 1;
    }

    @Override
    public int update(Worktime pojo) {
        this.getSession().update(pojo);
        return 1;
    }

    public int update(List<Worktime> l) {
        Session session = super.getSession();
        int currentRow = 1;
        for (Worktime a : l) {
            session.update(a);
            flushIfReachFetchSize(session, currentRow++);
        }
        return 1;
    }

    @Override
    public int delete(Worktime pojo) {
        this.getSession().delete(pojo);
        return 1;
    }

    public int deleteAll() {
        Session session = super.getSession();
        session.createQuery("delete from Worktime").executeUpdate();
        return 1;
    }

}
