/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.UnitDAO;
import com.advantech.model.db1.Unit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class UnitService {

    @Autowired
    private UnitDAO dao;

    public List<Unit> findAll() {
        return dao.findAll();
    }

    public Unit findByPrimaryKey(Object obj_id) {
        return dao.findByPrimaryKey(obj_id);
    }

    public int insert(Unit pojo) {
        return dao.insert(pojo);
    }

    public int update(Unit pojo) {
        return dao.update(pojo);
    }

    public int delete(Unit pojo) {
        return dao.delete(pojo);
    }

}
