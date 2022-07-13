/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.BabStandardTimeHistoryDAO;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabStandardTimeHistory;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.Worktime;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.criterion.Restrictions;
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
    private WorktimeService worktimeService;

    @Autowired
    private LineService lineService;

    public List<BabStandardTimeHistory> findAll() {
        return babStandardTimeHistoryDAO.findAll();
    }

    public BabStandardTimeHistory findByPrimaryKey(Object obj_id) {
        return babStandardTimeHistoryDAO.findByPrimaryKey(obj_id);
    }

    public BabStandardTimeHistory findByBab(int babId) {
        return this.babStandardTimeHistoryDAO.findByBab(babId);
    }

    public int insert(BabStandardTimeHistory pojo) {
        return babStandardTimeHistoryDAO.insert(pojo);
    }

    public int insertByBab(Bab b) {
        Worktime w = worktimeService.findByModelName(b.getModelName());
        LineType lineType = lineService.findLineType(b.getLine().getId());
        String lineTypeName = lineType.getName();
        BigDecimal standardTime = BigDecimal.ZERO;
        if (w != null) {
            switch (lineTypeName) {
                case "ASSY":
                    standardTime = w.getAssy();
                    break;
                case "Packing":
                    standardTime = w.getPacking();
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
