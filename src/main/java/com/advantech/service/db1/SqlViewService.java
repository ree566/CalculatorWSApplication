/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.SqlViewDAO;
import com.advantech.helper.PropertiesReader;
import com.advantech.model.db1.Bab;
import com.advantech.model.view.db1.BabAvg;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class SqlViewService {

    @Autowired
    private SqlViewDAO sqlViewDAO;

    @Autowired
    private PropertiesReader reader;

    public List<BabAvg> findBabAvg(int bab_id) {
        switch (reader.getBabDataCollectMode()) {
            case AUTO:
                return sqlViewDAO.findBabAvg(bab_id);
            case MANUAL:
                return sqlViewDAO.findBabAvgWithBarcode(bab_id);
            default:
                return new ArrayList();
        }
    }

    public List<BabAvg> findBabAvgInHistory(int bab_id) {
        return sqlViewDAO.findBabAvgInHistory(bab_id);
    }

    public List<Map> findSensorStatus(int bab_id) {
        return sqlViewDAO.findSensorStatus(bab_id);
    }

    public List<Map> findBarcodeStatus(int bab_id) {
        return sqlViewDAO.findBarcodeStatus(bab_id);
    }

    public List<Map> findBalanceDetail(int bab_id) {
        return sqlViewDAO.findBalanceDetail(bab_id);
    }

    public List<Map> findBalanceDetailWithBarcode(int bab_id) {
        return sqlViewDAO.findBalanceDetailWithBarcode(bab_id);
    }

    public List<Map> findSensorStatusPerStationToday() {
        return sqlViewDAO.findSensorStatusPerStationToday();
    }

    public List<Bab> findBabLastInputPerLine() {
        return sqlViewDAO.findBabLastInputPerLine();
    }

}
