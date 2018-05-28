/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao;

import com.advantech.model.Bab;
import com.advantech.model.LineBalancing;
import com.advantech.model.LineType;
import java.util.List;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class LineBalancingDAO extends AbstractDao<Integer, LineBalancing> implements BasicDAO_1<LineBalancing> {

    @Override
    public List<LineBalancing> findAll() {
        Query q = super.getSession().createSQLQuery("SELECT * FROM Line_Balancing_Main_F");
        return q.list();
    }

    @Override
    public LineBalancing findByPrimaryKey(Object obj_id) {
        return super.getByKey((int) obj_id);
    }

    public LineBalancing getMaxBalance(Bab bab, LineType lineType) {
        Query q = super.getSession().createQuery(
                "FROM LineBalancing where modelName = :modelName "
                + "and po = :po "
                + "and people = :people and lineType = :lineType "
                + "and balance is not null "
                + "and (avg1 is not null or avg1 != 0) "
                + "order by balance desc");
        q.setParameter("modelName", bab.getModelName());
        q.setParameter("po", bab.getPo());
        q.setParameter("people", bab.getPeople());
        q.setParameter("lineType", lineType.getName());
        q.setMaxResults(1);
        return (LineBalancing) q.uniqueResult();
    }

    @Override
    public int insert(LineBalancing pojo) {
        super.getSession().save(pojo);
        return 1;
    }

    @Override
    public int update(LineBalancing pojo) {
        super.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(LineBalancing pojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
