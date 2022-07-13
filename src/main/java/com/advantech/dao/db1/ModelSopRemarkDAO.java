/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.Line;
import com.advantech.model.db1.ModelSopRemark;
import com.advantech.model.db1.ModelSopRemarkDetail;
import com.advantech.model.db1.User;
import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class ModelSopRemarkDAO extends AbstractDao<Integer, ModelSopRemark> implements BasicDAO_1<ModelSopRemark> {

    @Override
    public List<ModelSopRemark> findAll() {
        return super.createEntityCriteria().list();
    }

    @Override
    public ModelSopRemark findByPrimaryKey(Object obj_id) {
        return super.getByKey((int) obj_id);
    }

    public List<Line> findUseLine(int id) {
        ModelSopRemark remark = this.findByPrimaryKey(id);
        return newArrayList(remark.getLines());
    }

    public List<ModelSopRemarkDetail> findDetail(int id) {
        ModelSopRemark remark = this.findByPrimaryKey(id);
        return newArrayList(remark.getModelSopRemarkDetails());
    }

    @Override
    public int insert(ModelSopRemark pojo) {
        super.getSession().save(pojo);
        return 1;
    }

    @Override
    public int update(ModelSopRemark pojo) {
        super.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(ModelSopRemark pojo) {
        super.getSession().delete(pojo);
        return 1;
    }

}
