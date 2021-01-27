/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.Bab;
import com.advantech.model.db1.Floor;
import com.advantech.model.view.BabLastBarcodeStatus;
import com.advantech.model.view.BabLastGroupStatus;
import com.advantech.model.view.PreAssyModuleUnexecuted;
import com.advantech.model.view.SensorCurrentGroupStatus;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class SqlProcedureDAO extends AbstractDao<Integer, Object> {

    public List<BabLastGroupStatus> findBabLastGroupStatus(int bab_id) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetLastGroupStatus(:bab_id)}")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.aliasToBean(BabLastGroupStatus.class))
                .list();
    }

    public List<BabLastBarcodeStatus> findBabLastBarcodeStatus(int bab_id) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetLastBarcodeStatus(:bab_id)}")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.aliasToBean(BabLastBarcodeStatus.class))
                .list();
    }

    //For job "CheckSensor.java" check sensor
    public List<SensorCurrentGroupStatus> findSensorCurrentGroupStatus(int bab_id) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetSensorCurrentGroupStatus(:bab_id)}")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.aliasToBean(SensorCurrentGroupStatus.class))
                .list();
    }

    //Join detail with alarmPercent in /pages/admin/BabTotal page
    public List<Map> findBabDetail(int lineType_id, int floor_id, DateTime sD, DateTime eD, boolean isAboveStandard) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetBabDetail_1(:lineType_id, :floor_id, :sD, :eD, :minPcs)}")
                .setParameter("lineType_id", lineType_id)
                .setParameter("floor_id", floor_id)
                .setParameter("sD", sD.withHourOfDay(0).toDate())
                .setParameter("eD", eD.withHourOfDay(23).toDate())
                .setParameter("minPcs", isAboveStandard ? 10 : "-1")
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    //Join detail with alarmPercent in /pages/admin/BabTotal page
    public List<Map> findBabDetailWithBarcode(int lineType_id, int floor_id, DateTime sD, DateTime eD, boolean isAboveStandard) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetBabDetail_WithBarcode(:lineType_id, :floor_id, :sD, :eD, :minPcs)}")
                .setParameter("lineType_id", lineType_id)
                .setParameter("floor_id", floor_id)
                .setParameter("sD", sD.withHourOfDay(0).toDate())
                .setParameter("eD", eD.withHourOfDay(23).toDate())
                .setParameter("minPcs", isAboveStandard ? 10 : "-1")
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    //Get bananceCompare with alarmPercent in /pages/admin/BabTotal page
    public List<Map> findLineBalanceCompareByBab(int bab_id) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetLineBalanceCompareByBab(:bab_id)}")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    //Get bananceCompare with alarmPercent in /pages/admin/BabTotal page
    public List<Map> findLineBalanceCompareByBabWithBarcode(int bab_id) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetLineBalanceCompareByBab_WithBarcode(:bab_id)}")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    //Get bananceCompare with alarmPercent in /pages/admin/BabTotal page
    public List<Map> findLineBalanceCompare(String modelName, String lineTypeName) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetLineBalanceCompare(:modelName, :lineTypeName)}")
                .setParameter("modelName", modelName)
                .setParameter("lineTypeName", lineTypeName)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBabPcsDetail(String modelName, String lineType, DateTime startDate, DateTime endDate) {

        if (startDate != null && endDate != null) {
            startDate = startDate.withHourOfDay(0);
            endDate = endDate.withHourOfDay(23);
        }

        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_Excel_PcsDetail(:modelName, :lineType, :startDate, :endDate)}")
                .setParameter("modelName", modelName)
                .setParameter("lineType", "-1".equals(lineType) ? null : lineType)
                .setParameter("startDate", startDate != null ? startDate.toDate() : startDate)
                .setParameter("endDate", endDate != null ? endDate.toDate() : endDate)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBabPcsDetailWithBarcode(String modelName, String lineType, DateTime startDate, DateTime endDate) {

        if (startDate != null && endDate != null) {
            startDate = startDate.withHourOfDay(0);
            endDate = endDate.withHourOfDay(23);
        }

        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_Excel_PcsDetail_WithBarcode(:modelName, :lineType, :startDate, :endDate)}")
                .setParameter("modelName", modelName)
                .setParameter("lineType", "-1".equals(lineType) ? null : lineType)
                .setParameter("startDate", startDate != null ? startDate.toDate() : startDate)
                .setParameter("endDate", endDate != null ? endDate.toDate() : endDate)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBabLineProductivity(String po, String modelName, Integer line_id, String jobnumber, Integer minPcs, DateTime sD, DateTime eD) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_Excel_LineProductivity(:po, :modelName, :lineId, :jobnumber, :minPcs, :sD, :eD)}")
                .setParameter("po", po)
                .setParameter("modelName", modelName)
                .setParameter("lineId", line_id)
                .setParameter("jobnumber", jobnumber)
                .setParameter("minPcs", minPcs)
                .setParameter("sD", sD.withHourOfDay(0).toDate())
                .setParameter("eD", eD.withHourOfDay(23).toDate())
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBabLineProductivityWithBarcode(String po, String modelName, Integer line_id, String jobnumber, Integer minPcs, DateTime sD, DateTime eD) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_Excel_LineProductivity_WithBarcode(:po, :modelName, :lineId, :jobnumber, :minPcs, :sD, :eD)}")
                .setParameter("po", po)
                .setParameter("modelName", modelName)
                .setParameter("lineId", line_id)
                .setParameter("jobnumber", jobnumber)
                .setParameter("minPcs", minPcs)
                .setParameter("sD", sD.withHourOfDay(0).toDate())
                .setParameter("eD", eD.withHourOfDay(23).toDate())
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBabPassStationRecord(String po, String modelName, DateTime sD, DateTime eD, String lineType) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetBabPassStationRecord_1(:po, :modelName, :sD, :eD, :lineType)}")
                .setParameter("po", po)
                .setParameter("modelName", modelName)
                .setParameter("sD", sD != null ? sD.withHourOfDay(0).toDate() : null)
                .setParameter("eD", eD != null ? eD.withHourOfDay(23).toDate() : null)
                .setParameter("lineType", lineType)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBabPassStationExceptionReport(String po, String modelName, DateTime sD, DateTime eD, int lineType_id) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_BabPassStation_ExceptionReport(:po, :modelName, :sD, :eD, :lineType_id)}")
                .setParameter("po", po)
                .setParameter("modelName", modelName)
                .setParameter("sD", sD != null ? sD.withHourOfDay(0).toDate() : null)
                .setParameter("eD", eD != null ? eD.withHourOfDay(23).toDate() : null)
                .setParameter("lineType_id", lineType_id)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBabPreAssyProductivity(int lineType_id, int floor_id, DateTime sD, DateTime eD) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetBabPreAssyProductivity(:lineType_id, :floor_id, :sD, :eD)}")
                .setParameter("lineType_id", lineType_id)
                .setParameter("floor_id", floor_id)
                .setParameter("sD", sD != null ? sD.withHourOfDay(0).toDate() : null)
                .setParameter("eD", eD != null ? eD.withHourOfDay(23).toDate() : null)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBabBestLineBalanceRecord(int lineType_id, DateTime sD, DateTime eD) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetBabBestLineBalanceRecord(:lineType_id, :sD, :eD)}")
                .setParameter("lineType_id", lineType_id)
                .setParameter("sD", sD != null ? sD.withHourOfDay(0).toDate() : null)
                .setParameter("eD", eD != null ? eD.withHourOfDay(23).toDate() : null)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<PreAssyModuleUnexecuted> findPreAssyModuleUnexecuted(DateTime sD, DateTime eD) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetPreAssyModuleUnexecuted(:sD, :eD)}")
                .setParameter("sD", sD != null ? sD.withHourOfDay(0).toDate() : null)
                .setParameter("eD", eD != null ? eD.withHourOfDay(23).toDate() : null)
                .setResultTransformer(Transformers.aliasToBean(PreAssyModuleUnexecuted.class))
                .list();
    }

    public List<Map> findTestPassStationProductivity(DateTime sD, DateTime eD) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_Excel_TestPassStationProductivity(:sD, :eD)}")
                .setParameter("sD", sD != null ? sD.withHourOfDay(0).toDate() : null)
                .setParameter("eD", eD != null ? eD.withHourOfDay(23).toDate() : null)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public int closeBabDirectly(Bab b) {
        super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_CloseBabDirectly(:bab_id)}")
                .setParameter("bab_id", b.getId())
                .executeUpdate();
        return 1;
    }

    public int closeBabWithSaving(Bab b) {
        super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_CloseBabWithSaving(:bab_id)}")
                .setParameter("bab_id", b.getId())
                .executeUpdate();
        return 1;
    }

    public int closeBabWithSavingWithBarcode(Bab b) {
        super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_CloseBabWithSavingWithBarcode(:bab_id)}")
                .setParameter("bab_id", b.getId())
                .executeUpdate();
        return 1;
    }

    public List<Map> getTotalAbnormalData(int bab_id) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.sensorTotalAbnormalCheck(:bab_id)}")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> getAbnormalData(int bab_id) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.sensorAbnormalCheck(?)}")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public int sensorDataClean(Date date) {
        super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_DeleteSensorData(:date)}")
                .setParameter("date", date)
                .executeUpdate();
        return 1;
    }

    public List<Map> findBabModuleUsageRateForAssy(DateTime sD, DateTime eD, Floor f) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetBabModuleUsageRate_ForAssy(:sD, :eD, :floor_id)}")
                .setParameter("sD", sD != null ? sD.withHourOfDay(0).toDate() : null)
                .setParameter("eD", eD != null ? eD.withHourOfDay(23).toDate() : null)
                .setParameter("floor_id", f == null ? null : f.getId())
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }
    
    public List<Map> findBabModuleUsageRateForPacking(DateTime sD, DateTime eD, Floor f) {
        return super.getSession()
                .createSQLQuery("{CALL M3_BW.usp_GetBabModuleUsageRate_ForPacking(:sD, :eD, :floor_id)}")
                .setParameter("sD", sD != null ? sD.withHourOfDay(0).toDate() : null)
                .setParameter("eD", eD != null ? eD.withHourOfDay(23).toDate() : null)
                .setParameter("floor_id", f == null ? null : f.getId())
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

}
