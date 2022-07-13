/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.WorktimeDAO;
import com.advantech.helper.PropertiesReader;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.Worktime;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class WorktimeService {

    @Autowired
    private WorktimeDAO dao;

    public List<Worktime> findAll() {
        return dao.findAll();
    }

    public Worktime findByPrimaryKey(Object obj_id) {
        return dao.findByPrimaryKey(obj_id);
    }

    public Worktime findByModelName(String modelName) {
        return dao.findByModelName(modelName);
    }

    public List<Worktime> findNotZeroPackingLeadTime() {
        return dao.findNotZeroPackingLeadTime();
    }

    public int insert(Worktime pojo) {
        return dao.insert(pojo);
    }

    public int insert(List<Worktime> l) {
        return dao.insert(l);
    }

    public int update(Worktime pojo) {
        return dao.update(pojo);
    }

    public int update(List<Worktime> l) {
        return dao.update(l);
    }

    public int delete(Worktime pojo) {
        return dao.delete(pojo);
    }

    public int deleteAll() {
        return dao.deleteAll();
    }

}
