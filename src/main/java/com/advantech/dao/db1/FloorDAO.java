/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.Floor;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class FloorDAO extends AbstractDao<Integer, Floor> implements BasicDAO_1<Floor> {

    @Override
    public List<Floor> findAll() {
        return super.createEntityCriteria().list();
    }

    @Override
    public Floor findByPrimaryKey(Object obj_id) {
        return super.getByKey((int) obj_id);
    }
    
    public List<Floor> findByPrimaryKeys(Integer... obj_ids){
        Criteria c = super.createEntityCriteria();
        c.add(Restrictions.in("id", obj_ids));
        return c.list();
    }

    public Floor findByName(String floorName) {
        Criteria c = super.createEntityCriteria();
        c.add(Restrictions.eq("name", floorName));
        return (Floor) c.uniqueResult();
    }

    @Override
    public int insert(Floor pojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(Floor pojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int delete(Floor pojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
