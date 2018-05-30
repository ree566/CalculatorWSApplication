/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service;

import com.advantech.dao.ModelSopRemarkDetailDAO;
import com.advantech.model.ModelSopRemarkDetail;
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
public class ModelSopRemarkDetailService {

    @Autowired
    private ModelSopRemarkDetailDAO modelSopRemarkDetailDAO;

    public List<ModelSopRemarkDetail> findAll() {
        return modelSopRemarkDetailDAO.findAll();
    }

    public ModelSopRemarkDetail findByPrimaryKey(Object obj_id) {
        return modelSopRemarkDetailDAO.findByPrimaryKey(obj_id);
    }

    public int insert(ModelSopRemarkDetail pojo) {
        return modelSopRemarkDetailDAO.insert(pojo);
    }

    public int update(ModelSopRemarkDetail pojo) {
        return modelSopRemarkDetailDAO.update(pojo);
    }

    public int delete(ModelSopRemarkDetail pojo) {
        return modelSopRemarkDetailDAO.delete(pojo);
    }

}