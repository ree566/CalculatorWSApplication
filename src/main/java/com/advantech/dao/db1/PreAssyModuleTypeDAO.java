/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.LineType;
import com.advantech.model.db1.PreAssyModuleType;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class PreAssyModuleTypeDAO extends AbstractDao<Integer, PreAssyModuleType> implements BasicDAO_1<PreAssyModuleType> {

    @Override
    public List<PreAssyModuleType> findAll() {
        return super.createEntityCriteria().list();
    }
    
    public List<PreAssyModuleType> findByModelName(String modelName) {
        return super.createEntityCriteria()
                .createAlias("preAssyModuleStandardTimes", "st")
                .add(Restrictions.eq("st.modelName", modelName))
                .list();
    }
    
    public List<PreAssyModuleType> findByModelNameAndLineType(String modelName, LineType lt) {
        return super.createEntityCriteria()
                .createAlias("preAssyModuleStandardTimes", "st")
                .add(Restrictions.eq("st.modelName", modelName))
                .add(Restrictions.eq("lineType", lt))
                .list();
    }

    @Override
    public PreAssyModuleType findByPrimaryKey(Object obj_id) {
        return super.getByKey((int) obj_id);
    }

    public List<PreAssyModuleType> findByPrimaryKeys(Integer... obj_ids) {
        Criteria c = super.createEntityCriteria();
        c.add(Restrictions.in("id", obj_ids));
        return c.list();
    }

    @Override
    public int insert(PreAssyModuleType pojo) {
        super.getSession().save(pojo);
        return 1;
    }

    @Override
    public int update(PreAssyModuleType pojo) {
        super.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(PreAssyModuleType pojo) {
        super.getSession().delete(pojo);
        return 1;
    }

}
