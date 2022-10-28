/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.Test;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class TestDAO extends AbstractDao<Integer, Test> implements BasicDAO_1<Test> {

    @Autowired
    private AlarmTestActionDAO alarmTestActionDAO;

    @Override
    public List<Test> findAll() {
        Criteria c = super.createEntityCriteria();
        c.setFetchMode("testTable", FetchMode.JOIN);
        c.setFetchMode("testTable.floor", FetchMode.JOIN);
        return c.list();
    }

    @Override
    public Test findByPrimaryKey(Object obj_id) {
        return super.getByKey((int) obj_id);
    }

    public Test findByJobnumber(String jobnumber) {
        Criteria c = super.createEntityCriteria();
        c.setFetchMode("testTable", FetchMode.JOIN);
        c.add(Restrictions.eq("userId", jobnumber));
        return (Test) c.uniqueResult();
    }

    @Override
    public int insert(Test pojo) {
        super.getSession().save(pojo);
        return 1;
    }

    @Override
    public int update(Test pojo) {
        super.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(Test pojo) {
        super.getSession().delete(pojo);
        return 1;
    }

    public int cleanTests() {
        Query q = super.getSession().createSQLQuery("truncate table {h-schema}LS_Test");
        q.executeUpdate();
        return 1;
    }

}
