/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service;

import com.advantech.dao.BabStandardTimeHistoryDAO;
import com.advantech.model.Bab;
import com.advantech.model.BabStandardTimeHistory;
import com.advantech.model.LineType;
import com.advantech.model.view.Worktime;
import java.math.BigDecimal;
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
public class BabStandardTimeHistoryService {

    @Autowired
    private BabStandardTimeHistoryDAO babStandardTimeHistoryDAO;

    @Autowired
    private SqlViewService sqlViewService;

    @Autowired
    private LineService lineService;

    public List<BabStandardTimeHistory> findAll() {
        return babStandardTimeHistoryDAO.findAll();
    }

    public BabStandardTimeHistory findByPrimaryKey(Object obj_id) {
        return babStandardTimeHistoryDAO.findByPrimaryKey(obj_id);
    }

    public int insert(BabStandardTimeHistory pojo) {
        return babStandardTimeHistoryDAO.insert(pojo);
    }

    public int insertByBab(Bab b) {
        Worktime w = sqlViewService.findWorktime(b.getModelName());
        LineType lineType = lineService.findLineType(b.getLine().getId());
        String lineTypeName = lineType.getName();
        BigDecimal standardTime = BigDecimal.ZERO;
        if (w != null) {
            switch (lineTypeName) {
                case "ASSY":
                    standardTime = w.getAssyTime();
                    break;
                case "Packing":
                    standardTime = w.getPackingTime();
                    break;
                default:
                    break;
            }
        }
        this.insert(new BabStandardTimeHistory(b, standardTime));
        return 1;
    }

    public int update(BabStandardTimeHistory pojo) {
        return babStandardTimeHistoryDAO.update(pojo);
    }

    public int delete(BabStandardTimeHistory pojo) {
        return babStandardTimeHistoryDAO.delete(pojo);
    }

}
