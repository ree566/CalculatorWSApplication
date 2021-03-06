/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.BabPcsDetailHistoryDAO;
import com.advantech.model.db1.BabPcsDetailHistory;
import java.util.List;
import java.util.Map;
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
public class BabPcsDetailHistoryService {

    @Autowired
    private BabPcsDetailHistoryDAO babPcsDetailHistoryDAO;

    public List<BabPcsDetailHistory> findByBab(int bab_id) {
        return babPcsDetailHistoryDAO.findByBab(bab_id);
    }

    public List<Map> findByBabForMap(int bab_id) {
        return babPcsDetailHistoryDAO.findByBabForMap(bab_id);
    }

    public List<BabPcsDetailHistory> findWithBabAndAlarmDetails(String modelName, String lineType, DateTime sD, DateTime eD) {
        return babPcsDetailHistoryDAO.findWithBabAndAlarmDetails(modelName, lineType, sD, eD);
    }

}
