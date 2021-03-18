/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.PrepareScheduleDailyRemarkDAO;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.PrepareScheduleDailyRemark;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class PrepareScheduleDailyRemarkService {

    @Autowired
    private PrepareScheduleDailyRemarkDAO dao;
    
    @Autowired
    private LineTypeService lineTypeService;

    public List<PrepareScheduleDailyRemark> findAll() {
        return dao.findAll();
    }

    public PrepareScheduleDailyRemark findByPrimaryKey(Object obj_id) {
        return dao.findByPrimaryKey(obj_id);
    }

    public List<PrepareScheduleDailyRemark> findByLineTypeAndDate(Integer[] lineType_id, DateTime dt) {
        return dao.findByLineTypeAndDate(lineTypeService.findByPrimaryKeys(lineType_id), dt);
    }

    public int insert(PrepareScheduleDailyRemark pojo) {
        return dao.insert(pojo);
    }

    public int update(PrepareScheduleDailyRemark pojo) {
        return dao.update(pojo);
    }

    public int delete(PrepareScheduleDailyRemark pojo) {
        return dao.delete(pojo);
    }

}
