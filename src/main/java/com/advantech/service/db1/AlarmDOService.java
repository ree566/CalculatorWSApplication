/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.AlarmDODAO;
import com.advantech.model.db1.AlarmDO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Justin.Yeh
 */
@Service
@Transactional
public class AlarmDOService {

    @Autowired
    private AlarmDODAO alarmDODAO;

    public List<AlarmDO> findAll() {
        return alarmDODAO.findAll();
    }

    public List<String> findProcessNameAll() {
        return findUniqueColumnAll("processName");
    }

    public List<String> findcorrespondDOAll() {
        return findUniqueColumnAll("correspondDO");
    }

    private <T> List<T> findUniqueColumnAll(String colName) {
        return alarmDODAO.findUniqueColumnAll(colName);
    }

    public List<AlarmDO> findDOByTables(List<String> tableIds) {
        List<AlarmDO> list = new ArrayList<>();
        try {
            list = alarmDODAO.findDOByTables(tableIds);        
        } catch (Exception ex) {
            throw ex;
        }
        return list;
    }

    public AlarmDO findByPrimaryKey(Object obj_id) {
        return alarmDODAO.findByPrimaryKey(obj_id);
    }

    public int insert(AlarmDO pojo) {
        return alarmDODAO.insert(pojo);
    }

    public int update(AlarmDO pojo) {
        return alarmDODAO.update(pojo);
    }

    public int delete(AlarmDO pojo) {
        return alarmDODAO.delete(pojo);
    }

}
