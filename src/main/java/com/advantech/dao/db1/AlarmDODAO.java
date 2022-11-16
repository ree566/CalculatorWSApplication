/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.AlarmDO;
import java.util.List;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Justin.Yeh
 */
@Repository
public class AlarmDODAO extends AbstractDao<String, AlarmDO> implements BasicDAO_1<AlarmDO> {

    @Override
    public List<AlarmDO> findAll() {
        return super.createEntityCriteria().list();
    }

    public <T> List<T> findUniqueColumnAll(String colName) {
        return super.createEntityCriteria()
                .setProjection(Projections.distinct(Projections.property(colName)))
                .list();
    }

    public List<AlarmDO> findDOByTables(List<String> tableIds) {
        return super.createEntityCriteria()
                .add(Restrictions.in("processName", tableIds))
                .list();
    }

    @Override
    public AlarmDO findByPrimaryKey(Object obj_id) {
        try {
            return super.getByKey((String) obj_id);
        } catch (Exception ex) {
            System.out.println("AlarmDO.findByPrimaryKey Exception : " + ex);
            throw ex;
        }
    }

    @Override
    public int insert(AlarmDO pojo) {
        super.getSession().save(pojo);
        return 1;
    }

    @Override
    public int update(AlarmDO pojo) {
        super.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(AlarmDO pojo) {
        super.getSession().delete(pojo);
        return 1;
    }

}
