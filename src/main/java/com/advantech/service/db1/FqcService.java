/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.FqcDAO;
import com.advantech.model.db1.BabStatus;
import com.advantech.model.db1.Fqc;
import com.advantech.model.db1.FqcLine;
import com.advantech.model.db1.FqcLoginRecord;
import com.advantech.model.db1.FqcModelStandardTime;
import com.advantech.model.db1.FqcProductivityHistory;
import com.advantech.model.db1.FqcSettingHistory;
import com.advantech.model.db1.FqcTimeTemp;
import com.advantech.model.db1.PassStationRecord;
import com.advantech.webservice.WebServiceRV;
import static com.google.common.base.Preconditions.*;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class FqcService {

    @Autowired
    private FqcDAO fqcDAO;

    @Autowired
    private FqcSettingHistoryService fqcSettingHistoryService;

    @Autowired
    private FqcLoginRecordService fqcLoginRecordService;

    @Autowired
    private PassStationRecordService passStationRecordService;

    @Autowired
    private FqcProductivityHistoryService fqcProducitvityHistoryService;

    @Autowired
    private FqcModelStandardTimeService fqcModelStandardTimeService;

    @Autowired
    private FqcTimeTempService fqcTempService;

    @Autowired
    private WebServiceRV rv;

    public List<Fqc> findAll() {
        return fqcDAO.findAll();
    }

    public Fqc findByPrimaryKey(Object obj_id) {
        return fqcDAO.findByPrimaryKey(obj_id);
    }

    public List<Fqc> findProcessing() {
        return fqcDAO.findProcessing();
    }

    public List<Fqc> findProcessing(int fqcLine_id) {
        return fqcDAO.findProcessing(fqcLine_id);
    }

    public List<Fqc> findProcessingWithLine() {
        return fqcDAO.findProcessingWithLine();
    }

    public List<Fqc> findReconnectable(int fqcLine_id, String po) {
        return fqcDAO.findReconnectable(fqcLine_id, po);
    }

    /*
        Check user is login and po is exist or not.
     */
    public int checkAndInsert(Fqc pojo) {
        checkProcessingModel(pojo);
        FqcLine fqcLine = pojo.getFqcLine();
        FqcLoginRecord loginRecord = fqcLoginRecordService.findByFqcLine(fqcLine.getId());
        checkArgument(loginRecord != null, "Can't find login record in this line");
        this.insert(pojo);
        String jobnumber = loginRecord.getJobnumber();
        FqcSettingHistory history = new FqcSettingHistory(pojo, jobnumber);
        history.setBeginTime(pojo.getBeginTime());
        fqcSettingHistoryService.insert(history);
        return 1;
    }

    public void checkProcessingModel(Fqc pojo) {
        FqcLine fqcLine = pojo.getFqcLine();
        List<Fqc> l = this.findProcessing(fqcLine.getId());
        Fqc f = l.stream().filter(o -> Objects.equals(pojo.getModelName(), o.getModelName()))
                .findFirst().orElse(null);
        checkArgument(f == null, "The modelName: " + pojo.getModelName() + " is already exist.");
    }

    public int reconnectAbnormal(Fqc fqc) {
        fqc.setBabStatus(null);
        fqc.setLastUpdateTime(null);
        fqcDAO.update(fqc);
        FqcSettingHistory history = fqcSettingHistoryService.findByFqc(fqc);
        history.setLastUpdateTime(null);
        fqcSettingHistoryService.update(history);

        FqcProductivityHistory pHistory = fqcProducitvityHistoryService.findByFqc(fqc.getId());
        fqcProducitvityHistoryService.delete(pHistory);
        
        commentOutTempRecord(fqc.getId());
        
        return 1;
    }

    //Update Fqc, FqcSettingHistory, Save passStationRecord
    public int stationComplete(int fqc_id, String remark, int timeCost) {
        Fqc pojo = this.findByPrimaryKey(fqc_id);
        checkArgument(pojo != null, "Can't find fqc object in database");

        FqcLine fqcLine = pojo.getFqcLine();

        Date now = new Date();
        pojo.setBabStatus(BabStatus.CLOSED);
        pojo.setLastUpdateTime(now);
        pojo.setRemark(remark == null || "".equals(remark.trim()) ? null : remark);

        FqcSettingHistory history = fqcSettingHistoryService.findByFqc(pojo);
        history.setLastUpdateTime(now);
        fqcSettingHistoryService.update(history);

        List<PassStationRecord> records = rv.getPassStationRecords(pojo.getPo(), 16, fqcLine.getFactory());
        records.removeIf(rec -> !history.getJobnumber().equals(rec.getUserNo())
                || rec.getCreateDate().before(pojo.getBeginTime())
                || rec.getCreateDate().after(pojo.getLastUpdateTime()));
        passStationRecordService.insert(records);

        String modelName = pojo.getModelName();

        FqcModelStandardTime standardTime = null;
        List<FqcModelStandardTime> l = fqcModelStandardTimeService.findAll();

        if (!l.isEmpty()) {
            standardTime = l.stream()
                    .filter(f -> modelName.contains(f.getModelNameCategory()))
                    .max(Comparator.comparing(s -> s.getModelNameCategory().length()))
                    .orElse(null);
        }

        this.update(pojo);
        fqcProducitvityHistoryService.insert(new FqcProductivityHistory(pojo, records.size(),
                timeCost, standardTime == null ? 0 : standardTime.getStandardTime()));
        
        commentOutTempRecord(pojo.getId());
        return 1;
    }

    public int addAbnormalReconnectableSignAndClose(int fqc_id, String remark, int timeCost) {
        this.stationComplete(fqc_id, remark, timeCost);
        Fqc pojo = this.findByPrimaryKey(fqc_id);
        pojo.setBabStatus(BabStatus.UNFINSHED_RECONNECTABLE);
        this.update(pojo);
        return 1;
    }

    public int pauseAndSaveEvent(int fqc_id, int timePeriod) {
        Fqc f = this.findByPrimaryKey(fqc_id);
        fqcTempService.insert(new FqcTimeTemp(f, timePeriod));
        f.setBabStatus(BabStatus.PAUSE);
        this.update(f);
        return 1;
    }

    public int resumeTime(int fqc_id) {
        Fqc f = this.findByPrimaryKey(fqc_id);
        f.setBabStatus(null);
        this.update(f);
        commentOutTempRecord(fqc_id);
        return 1;
    }

    public void commentOutTempRecord(int fqc_id) {
        List<FqcTimeTemp> l = fqcTempService.findByFqcIdIn(fqc_id);
        if (!l.isEmpty()) {
            FqcTimeTemp t = l.stream().filter(p -> p.getLastUpdateTime() == null).reduce((first, second) -> second).orElse(null);
            if (t != null) {
                t.setLastUpdateTime(new Date());
                fqcTempService.update(t);
            }
        }
    }

    public int insert(Fqc pojo) {
        return fqcDAO.insert(pojo);
    }

    public int update(Fqc pojo) {
        return fqcDAO.update(pojo);
    }

    public int delete(Fqc pojo) {
        return fqcDAO.delete(pojo);
    }

}
