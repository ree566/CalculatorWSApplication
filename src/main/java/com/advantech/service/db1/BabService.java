package com.advantech.service.db1;

import com.advantech.service.db2.LineBalancingService;
import com.advantech.dao.db1.BabDAO;
import com.advantech.helper.DateTimeGapFinder;
import com.advantech.helper.PropertiesReader;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabDataCollectMode;
import com.advantech.model.db1.BabSettingHistory;
import com.advantech.model.db1.BabStatus;
import com.advantech.model.db1.Line;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.TagNameComparison;
import com.advantech.model.view.BabAvg;
import com.advantech.model.view.BabProcessDetail;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.google.common.base.Preconditions.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.annotation.PostConstruct;
import org.hibernate.Hibernate;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.transaction.annotation.Transactional;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class BabService {

    @Autowired
    private BabDAO babDAO;

    @Autowired
    private LineBalancingService lineBalancingService;

    @Autowired
    private BabSettingHistoryService babSettingHistoryService;

    @Autowired
    private BabStandardTimeHistoryService babStandardTimeHistoryService;

    @Autowired
    private SqlViewService sqlViewService;

    @Autowired
    private PropertiesReader p;

    private boolean isResultWriteToOldDatabase;

    private BabDataCollectMode mode;

    @Autowired
    private PreAssyModuleStandardTimeService preStandardTimeService;

    @Autowired
    private DateTimeGapFinder finder;

    @Autowired
    private LineTypeService lineTypeService;

    @Autowired
    private SqlProcedureService procService;

    @PostConstruct
    private void init() {
        isResultWriteToOldDatabase = p.getIsResultWriteToOldDatabase();
        mode = p.getBabDataCollectMode();
    }

    public List<Bab> findAll() {
        return babDAO.findAll();
    }

    public Bab findByPrimaryKey(Object obj_id) {
        return babDAO.findByPrimaryKey(obj_id);
    }

    public List<Bab> findByPrimaryKeys(Integer... obj_ids) {
        return babDAO.findByPrimaryKeys(obj_ids);
    }

    public Bab findWithLineInfo(int bab_id) {
        return babDAO.findWithLineInfo(bab_id);
    }

    public List<Bab> findByDate(DateTime sD, DateTime eD) {
        return babDAO.findByDate(sD, eD);
    }

    public List<Bab> findByModelAndDate(String modelName, DateTime sD, DateTime eD) {
        return babDAO.findByModelAndDate(modelName, sD, eD);
    }

    public List<Bab> findTodays() {
        DateTime d = new DateTime();
        return this.findByDate(d, d);
    }

    public List<Bab> findByMultipleClause(DateTime sD, DateTime eD, int lineType_id, int floor_id, boolean isAboveTenPcs) {
        return babDAO.findByMultipleClause(sD, eD, lineType_id, floor_id, isAboveTenPcs);
    }

    public List<Bab> findProcessing() {
        return babDAO.findProcessing();
    }

    public List<Bab> findProcessingAndNotPre() {
        return babDAO.findProcessingAndNotPre();
    }

    public List<Map> findProcessingByTagName(String tagName) {
        List<Bab> l = babDAO.findProcessingByTagName(tagName);
        List<Map> result = new ArrayList();
        l.forEach(b -> {
            Map m = new HashMap();
            if (b.getIspre() == 1) {
                m.put("standardTimes", preStandardTimeService.findByBab(b));
            }
            m.put("bab", b);
            result.add(m);
        });
        return result;
    }

    public List<String> findAllModelName() {
        return babDAO.findAllModelName();
    }

    public List<Bab> findUnReplyed(int floor_id, DateTime sD, DateTime eD) {
        List<Bab> l = babDAO.findUnReplyed(floor_id, sD, eD);
        l.forEach(b -> {
            Hibernate.initialize(b.getBabAlarmHistorys());
            Hibernate.initialize(b.getLine().getUsers());
        });
        return l;
    }

    public List<BabProcessDetail> findBabTimeGapPerLine(DateTime sD, DateTime eD) {

        DateTimeFormatter dateOnly_fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        sD = sD.withTime(8, 30, 0, 0);
        eD = eD.withTime(17, 30, 0, 0);
        List<LineType> lt = lineTypeService.findByPrimaryKeys(1, 3);
        List<Bab> l = babDAO.findByDateAndLineType(sD, eD, lt);

        l = l.stream().filter(b -> !isExcludeRecord(b)).collect(toList());

        Map<Integer, Map<String, List<Bab>>> result = l.stream()
                .collect(Collectors.groupingBy(b -> b.getLine().getId(),
                        Collectors.groupingBy(b -> dateOnly_fmt.print(new DateTime(b.getBeginTime()))))
                );

        List<BabProcessDetail> gapDetailsSum = new ArrayList();

        result.forEach((k, v) -> {
            v.forEach((k1, v1) -> {
                Interval rest1 = new Interval(new DateTime(k1).withTime(12, 0, 0, 0), new DateTime(k1).withTime(12, 50, 0, 0));
                Interval rest2 = new Interval(new DateTime(k1).withTime(15, 30, 0, 0), new DateTime(k1).withTime(15, 40, 0, 0));

                DateTime sDOD = new DateTime(k1).withTime(8, 30, 0, 0);
                DateTime eDOD = new DateTime(k1).withTime(17, 30, 0, 0);
//                int minInDay = Minutes.minutesBetween(sDOD, eDOD).getMinutes();

                List<Interval> gaps = this.searchGaps(v1, sDOD, eDOD);
                int totalMinutes = 0;

                //Exclude the rest of time.
                totalMinutes = gaps.stream().map((i) -> {
                    int total = Minutes.minutesBetween(i.getStart(), i.getEnd()).getMinutes();
                    if (i.overlaps(rest1)) {
                        Interval ri = i.overlap(rest1);
                        total -= Minutes.minutesBetween(ri.getStart(), ri.getEnd()).getMinutes();
                    }

                    if (i.overlaps(rest2)) {
                        Interval ri = i.overlap(rest2);
                        total -= Minutes.minutesBetween(ri.getStart(), ri.getEnd()).getMinutes();
                    }
                    return total;

                }).reduce(totalMinutes, Integer::sum);

                BabProcessDetail detail = new BabProcessDetail();
                detail.setLineId(k);
                detail.setDateString(k1);
                detail.setBabs(v1);
                detail.setIntervals(gaps);
                detail.setTotalGapsTimeInDay(totalMinutes);
                gapDetailsSum.add(detail);
            });
        });

        return gapDetailsSum;
    }

    public List<Bab> findByModelNames(List<String> modelNames) {
        return babDAO.findByModelNames(modelNames);
    }

    public List<Bab> findByModelNamesAndLines(List<String> modelNames, List<Line> lines) {
        return babDAO.findByModelNamesAndLines(modelNames, lines);
    }

    public List<Interval> searchGaps(List<Bab> l, DateTime startTimeOfDay, DateTime endTimeOfDay) {

        //Turn startDate & endDate into Interval object
        List<Interval> existingIntervals = new ArrayList();
        l.forEach(b -> {
            existingIntervals.add(new Interval(new DateTime(b.getBeginTime()), new DateTime(b.getLastUpdateTime())));
        });

        List<Interval> mergedIntervals = finder.mergeIntervals(existingIntervals);

        if (mergedIntervals == null) {
            mergedIntervals = new ArrayList();
        }

        Interval workTimeInDay = new Interval(startTimeOfDay, endTimeOfDay);
        List<Interval> bigSearchResults = finder.findGaps(mergedIntervals, workTimeInDay);

        return bigSearchResults;
    }

    private boolean isExcludeRecord(Bab b) {
        DateTime t = new DateTime(b.getBeginTime());
        DateTime sT = t.withTime(8, 30, 0, 0);
        DateTime eT = t.withTime(17, 30, 0, 0);
        return b.getBabStatus() == BabStatus.UNFINSHED
                || (b.getIspre() == 0 && b.getBabStatus() == BabStatus.NO_RECORD)
                || !new Interval(sT, eT).contains(t);
    }

    public int insert(Bab pojo) {
        return babDAO.insert(pojo);
    }

    public int checkAndInsert(Bab b, TagNameComparison tag) {
        BabSettingHistory bsh = babSettingHistoryService.findProcessingByTagName(tag.getId().getLampSysTagName().getName());
        if (bsh != null) {
            Bab processingBab = bsh.getBab();
            checkArgument(!Objects.equals(processingBab.getPo(), b.getPo()), "工單號碼已經存在");
        }
        babDAO.insert(b);
        babSettingHistoryService.insertByBab(b, tag);
        return 1;
    }

    public int update(Bab pojo) {
        return babDAO.update(pojo);
    }

    public int stationComplete(Bab bab, BabSettingHistory setting) {
        return this.stationComplete(bab, setting, true);
    }

    public int stationComplete(Bab bab, BabSettingHistory setting, boolean isNeedCheckPrev) {
        if (isNeedCheckPrev) {
            BabSettingHistory prev = babSettingHistoryService.findByBabAndStation(bab, setting.getStation() - 1);

            if (setting.getStation() == 2 && prev.getLastUpdateTime() == null) {
                prev.setLastUpdateTime(new Date());
                babSettingHistoryService.update(prev);
            } else {
                checkArgument(prev.getLastUpdateTime() != null, "關閉失敗，請檢查上一站是否關閉");
            }
        }

        setting.setLastUpdateTime(new Date());
        babSettingHistoryService.update(setting);

        return 1;

    }

    /*
        檢查統計值是否為空，空值直接讓使用者作結束
        檢查上一顆sensor使否有在紀錄中，有把紀錄記在LineBalancingMain
        所有儲存動作已經交由usp_CloseBabWithSaving
        不需要在code做其他動作(除了save avg data into Line_Balancing.dbo.Line_Balancing_Main table)
     */
    public int closeBab(Bab bab) {
        boolean needToSave = false;
        List<BabSettingHistory> babSettings = babSettingHistoryService.findByBab(bab);

        if (bab.getIspre() == 0) {
            List<BabAvg> babAvgs = sqlViewService.findBabAvg(bab.getId()); //先各站別取平衡率再算平均
            if (babAvgs != null && !babAvgs.isEmpty() && babAvgs.size() == bab.getPeople()) {
                bab.setBabAvgs(babAvgs);
                if (bab.getPeople() > 2) {
                    BabSettingHistory prev = babSettings.stream()
                            .filter(b -> b.getStation() == bab.getPeople() - 1)
                            .reduce((first, second) -> second).orElse(null);
                    checkArgument(prev.getLastUpdateTime() != null, "關閉失敗，請檢查上一站是否關閉");
                }
                needToSave = true;
            }
        }

        if (needToSave) {
            this.closeBabWithSaving(bab);

            //額外將當下工時紀錄
            babStandardTimeHistoryService.insertByBab(bab);
        } else {
            this.closeBabDirectly(bab);
        }

        BabSettingHistory setting = babSettings.stream()
                .filter(b -> b.getStation() == bab.getPeople())
                .reduce((first, second) -> second).orElse(null);
        this.stationComplete(bab, setting, needToSave == true && bab.getPeople() != 1);

        return 1;
    }

    public int closeBabDirectly(Bab b) {
        return procService.closeBabDirectly(b);
    }

    public int closeBabWithSaving(Bab b) {
        switch (mode) {
            case AUTO:
                procService.closeBabWithSaving(b);
                break;
            case MANUAL:
                procService.closeBabWithSavingWithBarcode(b);
                break;
            default:
                throw new IllegalStateException("BabDataCollectMode doesn't exist");
        }
        if (isResultWriteToOldDatabase) {
            lineBalancingService.insert(b);
        }
        return 1;
    }

    public int delete(Bab pojo) {
        return babDAO.delete(pojo);
    }

}
