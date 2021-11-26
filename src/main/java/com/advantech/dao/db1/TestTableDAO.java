/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.TestTable;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class TestTableDAO extends AbstractDao<Integer, TestTable> implements BasicDAO_1<TestTable> {

    @Override
    public List<TestTable> findAll() {
        return super.createEntityCriteria().list();
    }

    @Override
    public TestTable findByPrimaryKey(Object obj_id) {
        return super.getByKey((int) obj_id);
    }

    public List<TestTable> findBySitefloor(String floorName) {
        Criteria c = super.createEntityCriteria();
        c.createAlias("floor", "f");
        c.add(Restrictions.eq("f.name", floorName));
        return c.list();
    }

    public TestTable findByJobnumber(String jobnumber) {
        Criteria c = super.createEntityCriteria();
        c.add(Restrictions.eq("jobnumber", jobnumber));
        return (TestTable) c.uniqueResult();
    }

    @Override
    public int insert(TestTable pojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(TestTable pojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int delete(TestTable pojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
