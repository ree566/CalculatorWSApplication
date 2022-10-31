/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.controller;

import com.advantech.datatable.DataTableResponse;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabStatus;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Worktime;
import com.advantech.model.view.db1.BabAvg;
import com.advantech.service.db1.BabService;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db2.LineBalancingService;
import com.advantech.service.db1.SqlProcedureService;
import com.advantech.service.db1.SqlViewService;
import com.advantech.service.db1.WorktimeService;
import static com.google.common.base.Preconditions.checkArgument;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Wei.Cheng
 */
@Controller
@RequestMapping(value = "/SqlViewController")
public class SqlViewController {

    @Autowired
    private BabService babService;

    @Autowired
    private SqlViewService sqlViewService;

    @Autowired
    private LineBalancingService lineBalancingService;

    @Autowired
    private ModelController modelController;

    @Autowired
    private SqlProcedureService procSerice;

    @Autowired
    private WorktimeService worktimeService;

    @Autowired
    private FloorService floorService;

    @RequestMapping(value = "/findBabDetail", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findBabDetail(
            @RequestParam int lineType_id,
            @RequestParam int floor_id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate,
            @RequestParam boolean isAboveStandard) {

        List l = procSerice.findBabDetail(lineType_id, floor_id, startDate, endDate, isAboveStandard);
        return new DataTableResponse(l);

    }

    @RequestMapping(value = "/findLineBalanceCompareByBab", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findLineBalanceCompare(@RequestParam int bab_id) {
        Bab b = babService.findByPrimaryKey(bab_id);
        List<Map> l = procSerice.findLineBalanceCompareByBab(bab_id);
        Map m = l.get(0);

        m.put("exp_avgs",
                b.getBabStatus() == BabStatus.CLOSED ? getAvg(sqlViewService.findBabAvgInHistory(bab_id))
                : b.getBabStatus() == null ? getAvg(sqlViewService.findBabAvg(bab_id)) : 0);

        Integer ctrl_bab = (Integer) m.get("ctrl_id");
        m.put("ctrl_avgs", ctrl_bab == null ? 0 : getAvg(sqlViewService.findBabAvgInHistory(ctrl_bab)));
        return new DataTableResponse(l);
    }

    private Double getAvg(List<BabAvg> l) {
        return lineBalancingService.caculateLineBalance(l);
    }

    @RequestMapping(value = "/findLineBalanceCompare", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findLineBalanceCompare(
            @RequestParam String modelName,
            @RequestParam String lineTypeName) {

        List l = procSerice.findLineBalanceCompare(modelName, lineTypeName);
        return new DataTableResponse(l);

    }

    @RequestMapping(value = "/findSensorStatusPerStationToday", method = {RequestMethod.GET})
    @ResponseBody
    public DataTableResponse findSensorStatusPerStationToday() {
        return new DataTableResponse(sqlViewService.findSensorStatusPerStationToday());
    }

    @RequestMapping(value = "/findBabPcsDetail", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findBabPcsDetail(
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) String lineType,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate) {

        if (modelName == null || "".equals(modelName.trim())) {
            modelName = null;
        }

        if ("-1".equals(lineType)) {
            lineType = null;
        }

        //modelName 或 lineType 為空時一定要設定 startDate & endDate
        checkArgument(!((modelName == null || lineType == null) && (startDate == null || endDate == null)),
                "\"ModelName\"或\"類別\"為空時，一定要設定\"startDate\"&\"endDate\"");

        if (modelName == null && startDate != null && endDate != null) {
            checkArgument(Weeks.weeksBetween(startDate.toLocalDate(), endDate.toLocalDate()).getWeeks() <= 2,
                    "日期區間不得超過2週");
        }

        return new DataTableResponse(procSerice.findBabPcsDetail(modelName, lineType, startDate, endDate));
    }

    @RequestMapping(value = "/findBabLineProductivity", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findBabLineProductivity(
            @RequestParam(required = false) String po,
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) Integer line_id,
            @RequestParam(required = false) Integer lineType_id,
            @RequestParam(required = false) String jobnumber,
            @RequestParam(required = false) Integer minPcs,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate) {

        return new DataTableResponse(procSerice.findBabLineProductivity(po, modelName, line_id, lineType_id, jobnumber, minPcs, startDate, endDate));

    }

    @RequestMapping(value = "/findBabPassStationRecord", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findBabPassStationRecord(
            @RequestParam(required = false) String po,
            @RequestParam(required = false) String modelName,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate,
            @RequestParam String lineType) {

        return new DataTableResponse(procSerice.findBabPassStationRecord(po, modelName, startDate, endDate, lineType));

    }

    @RequestMapping(value = "/findBabPassStationExceptionReport", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findBabPassStationExceptionReport(
            @RequestParam(required = false) String po,
            @RequestParam(required = false) String modelName,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate,
            @RequestParam int lineType_id) {

        return new DataTableResponse(procSerice.findBabPassStationExceptionReport(po, modelName, startDate, endDate, lineType_id));

    }

    @RequestMapping(value = "/findBabPreAssyProductivity", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findBabPreAssyProductivity(
            @RequestParam(required = false) int lineType_id,
            @RequestParam(required = false) int floor_id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate) {

        return new DataTableResponse(procSerice.findBabPreAssyProductivity(lineType_id, floor_id, startDate, endDate));

    }

    @RequestMapping(value = "/findBabBestLineBalanceRecord", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findBabBestLineBalanceRecord(
            @RequestParam(required = false) int lineType_id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate) {
        
        return new DataTableResponse(procSerice.findBabBestLineBalanceRecord(lineType_id, startDate, endDate));
        
    }

    @RequestMapping(value = "/calculateChangeover", method = {RequestMethod.GET})
    @ResponseBody
    protected Map calculateChangeover(
            @RequestParam String po,
            @RequestParam int maxChangeover,
            @RequestParam(required = false) Integer people,
            @RequestParam int lineType) {
        
        Map m = new HashMap();
        String modelName = modelController.findModelNameByPo(po);
        if (modelName != null) {
            Worktime w = this.worktimeService.findByModelName(modelName);
            if (w != null) {
                BigDecimal worktime = lineType == 1 ? w.getAssy() : w.getPacking();
                people = (people == null ? (lineType == 1 ? w.getAssyPeople() : w.getPackingPeople()) : people);
                m.put("po", po);
                m.put("modelName", modelName);
                m.put("people", people);
                m.put("maxChangeover", maxChangeover);
                m.put("worktime", worktime);

                BigDecimal result = worktime.divide(new BigDecimal(people), 2, RoundingMode.HALF_DOWN)
                        .add(new BigDecimal(maxChangeover).divide(new BigDecimal(people), 2, RoundingMode.HALF_DOWN));
                m.put("result", result);
            }
        }
        return m;
        
    }

    @RequestMapping(value = "/findTestPassStationProductivity", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findTestPassStationProductivity(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate) {
        
        return new DataTableResponse(procSerice.findTestPassStationProductivity(startDate, endDate));
        
    }

    @RequestMapping(value = "/findBabModuleUsageRate", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findBabModuleUsageRate(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime endDate,
            @RequestParam int floor_id,
            @RequestParam int lineType_id) {
        
        Floor f = floor_id == -1 ? null : floorService.findByPrimaryKey(floor_id);
        List l;
        if (lineType_id == 1) {
            l = procSerice.findBabModuleUsageRateForAssy(startDate, endDate, f);
        } else if (lineType_id == 3) {
            l = procSerice.findBabModuleUsageRateForPacking(startDate, endDate, f);
        } else {
            l = new ArrayList();
        }
        return new DataTableResponse(l);
        
    }
    
    @RequestMapping(value = "/findPreAssyPercentage", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findPreAssyPercentage(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate) {
        

        List l = procSerice.findPreAssyPercentage(startDate);
 
        return new DataTableResponse(l);
        
    }

}
