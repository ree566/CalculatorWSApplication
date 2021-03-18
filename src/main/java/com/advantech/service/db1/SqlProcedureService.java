/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.SqlProcedureDAO;
import com.advantech.helper.PropertiesReader;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.ModelSopRemarkDetail;
import com.advantech.model.view.db1.BabLastBarcodeStatus;
import com.advantech.model.view.db1.BabLastGroupStatus;
import com.advantech.model.view.db1.PreAssyModuleUnexecuted;
import com.advantech.model.view.db1.SensorCurrentGroupStatus;
import java.util.ArrayList;
import java.util.Date;
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
public class SqlProcedureService {

    @Autowired
    private SqlProcedureDAO sqlProcedureDAO;

    @Autowired
    private PropertiesReader reader;

    public List<BabLastGroupStatus> findBabLastGroupStatus(int bab_id) {
        return sqlProcedureDAO.findBabLastGroupStatus(bab_id);
    }

    public List<BabLastGroupStatus> findBabLastGroupStatus(List<Bab> babs) {
        List<BabLastGroupStatus> l = new ArrayList();
        babs.forEach((b) -> {
            l.addAll(sqlProcedureDAO.findBabLastGroupStatus(b.getId()));
        });
        return l;
    }

    public List<BabLastBarcodeStatus> findBabLastBarcodeStatus(List<Bab> babs) {
        List<BabLastBarcodeStatus> l = new ArrayList();
        babs.forEach((b) -> {
            l.addAll(sqlProcedureDAO.findBabLastBarcodeStatus(b.getId()));
        });
        return l;
    }

    public List<SensorCurrentGroupStatus> findSensorCurrentGroupStatus(int bab_id) {
        return sqlProcedureDAO.findSensorCurrentGroupStatus(bab_id);
    }

    public List<Map> findBabDetail(int lineType_id, int floor_id, DateTime sD, DateTime eD, boolean isAboveStandard) {
        switch (reader.getBabDataCollectMode()) {
            case AUTO:
                return sqlProcedureDAO.findBabDetail(lineType_id, floor_id, sD, eD, isAboveStandard);
            case MANUAL:
                return sqlProcedureDAO.findBabDetailWithBarcode(lineType_id, floor_id, sD, eD, isAboveStandard);
            default:
                return new ArrayList();
        }
    }

    public List<Map> findLineBalanceCompareByBab(int bab_id) {
        return sqlProcedureDAO.findLineBalanceCompareByBab(bab_id);
    }

    public List<Map> findLineBalanceCompareByBabWithBarcode(int bab_id) {
        return sqlProcedureDAO.findLineBalanceCompareByBabWithBarcode(bab_id);
    }

    public List<Map> findLineBalanceCompare(String modelName, String lineTypeName) {
        return sqlProcedureDAO.findLineBalanceCompare(modelName, lineTypeName);
    }

    public List<Map> findBabPcsDetail(String modelName, String lineType, DateTime startDate, DateTime endDate) {
        switch (reader.getBabDataCollectMode()) {
            case AUTO:
                return sqlProcedureDAO.findBabPcsDetail(modelName, lineType, startDate, endDate);
            case MANUAL:
                return sqlProcedureDAO.findBabPcsDetailWithBarcode(modelName, lineType, startDate, endDate);
            default:
                return new ArrayList();
        }
    }

    public List<Map> findBabLineProductivity(String po, String modelName, int line_id, String jobnumber, Integer minPcs, DateTime sD, DateTime eD) {
        switch (reader.getBabDataCollectMode()) {
            case AUTO:
                return sqlProcedureDAO.findBabLineProductivity(po, modelName, line_id, jobnumber, minPcs, sD, eD);
            case MANUAL:
                return sqlProcedureDAO.findBabLineProductivityWithBarcode(po, modelName, line_id, jobnumber, minPcs, sD, eD);
            default:
                return new ArrayList();
        }
    }

    public List<Map> findBabPassStationRecord(String po, String modelName, DateTime sD, DateTime eD, String lineType) {
        return sqlProcedureDAO.findBabPassStationRecord(po, modelName, sD, eD, lineType);
    }

    public List<Map> findBabPassStationExceptionReport(String po, String modelName, DateTime sD, DateTime eD, int lineType_id) {
        return sqlProcedureDAO.findBabPassStationExceptionReport(po, modelName, sD, eD, lineType_id);
    }

    public List<Map> findBabPreAssyProductivity(int lineType_id, int floor_id, DateTime sD, DateTime eD) {
        return sqlProcedureDAO.findBabPreAssyProductivity(lineType_id, floor_id, sD, eD);
    }

    public List<Map> findBabBestLineBalanceRecord(int lineType_id, DateTime sD, DateTime eD) {
        return sqlProcedureDAO.findBabBestLineBalanceRecord(lineType_id, sD, eD);
    }

    public List<PreAssyModuleUnexecuted> findPreAssyModuleUnexecuted(DateTime sD, DateTime eD) {
        return sqlProcedureDAO.findPreAssyModuleUnexecuted(sD, eD);
    }

    public List<Map> findTestPassStationProductivity(DateTime sD, DateTime eD) {
        return sqlProcedureDAO.findTestPassStationProductivity(sD, eD);
    }

    public int closeBabDirectly(Bab b) {
        return sqlProcedureDAO.closeBabDirectly(b);
    }

    public int closeBabWithSaving(Bab b) {
        return sqlProcedureDAO.closeBabWithSaving(b);
    }

    public int closeBabWithSavingWithBarcode(Bab b) {
        return sqlProcedureDAO.closeBabWithSavingWithBarcode(b);
    }

    public List<Map> getTotalAbnormalData(int bab_id) {
        return sqlProcedureDAO.getTotalAbnormalData(bab_id);
    }

    public List<Map> getAbnormalData(int bab_id) {
        return sqlProcedureDAO.getAbnormalData(bab_id);
    }

    public int sensorDataClean(Date date) {
        return sqlProcedureDAO.sensorDataClean(date);
    }

    public List<Map> findBabModuleUsageRateForAssy(DateTime sD, DateTime eD, Floor f) {
        return sqlProcedureDAO.findBabModuleUsageRateForAssy(sD, eD, f);
    }
    
    public List<Map> findBabModuleUsageRateForPacking(DateTime sD, DateTime eD, Floor f) {
        return sqlProcedureDAO.findBabModuleUsageRateForPacking(sD, eD, f);
    }

}
