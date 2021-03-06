/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.LineTypeDAO;
import com.advantech.model.db1.ActionCode;
import com.advantech.model.db1.LineType;
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
public class LineTypeService {

    @Autowired
    private LineTypeDAO lineTypeDAO;

    public List<LineType> findAll() {
        return lineTypeDAO.findAll();
    }

    public LineType findByPrimaryKey(Object obj_id) {
        return lineTypeDAO.findByPrimaryKey(obj_id);
    }

    public List<LineType> findByPrimaryKeys(Integer... obj_ids) {
        return lineTypeDAO.findByPrimaryKeys(obj_ids);
    }

    public LineType findByName(String lineTypeName) {
        return lineTypeDAO.findByName(lineTypeName);
    }

    public int insert(LineType pojo) {
        return lineTypeDAO.insert(pojo);
    }

    public int update(LineType pojo) {
        return lineTypeDAO.update(pojo);
    }

    public int delete(LineType pojo) {
        return lineTypeDAO.delete(pojo);
    }

}
