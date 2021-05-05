/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.controller;

import com.advantech.converter.Encodeable;
import com.advantech.datatable.DataTableResponse;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.PrepareSchedule;
import com.advantech.model.view.db4.MesChangeTimeInfo;
import com.advantech.model.view.db4.MesPassStationInfo;
import com.advantech.quartzJob.ArrangePrepareScheduleImpl;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.LineTypeService;
import com.advantech.service.db1.PrepareScheduleDailyRemarkService;
import com.advantech.service.db1.PrepareScheduleService;
import com.advantech.service.db4.SqlViewService;
import com.advantech.webservice.mes.Section;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Wei.Cheng
 */
@Controller
@RequestMapping(value = "/PrepareScheduleController")
public class PrepareScheduleController {

    @Autowired
    private PrepareScheduleService wService;

    @Autowired
    private FloorService floorService;

    @Autowired
    private ArrangePrepareScheduleImpl aps1;

    @Autowired
    private PrepareScheduleService psService;

    @Autowired
    private LineTypeService lineTypeService;

    @Autowired
    private PrepareScheduleDailyRemarkService psRemarkService;

    private List<StationInfo> stationMap;

    @Autowired
    @Qualifier("sqlViewService4")
    private SqlViewService sqlViewService;

    private class StationInfo {

        public List<Integer> lineTypeIds;
        public List<Station> stations;
        public Section section;

        public StationInfo(List<Integer> lineTypeIds, List<Station> stations, Section section) {
            this.lineTypeIds = lineTypeIds;
            this.stations = stations;
            this.section = section;
        }

    }

    @PostConstruct
    protected void init() {
        stationMap = newArrayList(
                new StationInfo(newArrayList(9), newArrayList(Station.PREASSY), Section.PREASSY),
                new StationInfo(newArrayList(1), newArrayList(Station.ASSY), Section.BAB),
                new StationInfo(newArrayList(7), newArrayList(Station.T1, Station.BI), Section.TEST),
                new StationInfo(newArrayList(8), newArrayList(Station.T2, Station.T3, Station.T4), Section.TEST),
                new StationInfo(newArrayList(3), newArrayList(Station.PACKAGE), Section.PACKAGE)
        );
    }

    @RequestMapping(value = "/findPrepareSchedule", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findPrepareSchedule(
            @RequestParam Integer floorId,
            @RequestParam("lineType_id[]") Integer[] lineType_id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime d,
            HttpServletRequest request
    ) {
        Floor f = floorService.findByPrimaryKey(floorId);
        List l = aps1.findPrepareSchedule(f, lineType_id, d);
        return new DataTableResponse(l);
    }

    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    protected String update(
            @ModelAttribute PrepareSchedule pojo
    ) {
        wService.updateAndResortPriority(pojo);
        return "success";
    }

    @RequestMapping(value = "/splitCnt", method = {RequestMethod.POST})
    @ResponseBody
    protected String splitCnt(
            @RequestParam int id,
            @RequestParam List<Integer> cnt
    ) {

        PrepareSchedule pojo = wService.findByPrimaryKey(id);
        checkState(pojo != null, "Can't find po in schedule " + id);
        checkState(isInCurrentDate(pojo.getOnBoardDate()), "Onboard date invalid " + id);

        wService.separateCnt(pojo, cnt);

        return "success";

    }

    private boolean isInCurrentDate(Date d1) {
        DateTime dd1 = new DateTime(d1).withTime(0, 0, 0, 0);
        DateTime dd2 = new DateTime().withTime(0, 0, 0, 0);
        return dd1.isEqual(dd2);
    }

    @RequestMapping(value = "/findPrepareSchedulePercentage", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findPrepareSchedulePercentage(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam("lineType_id[]") Integer[] lineType_id,
            HttpServletRequest request
    ) {
        return new DataTableResponse(getData(startDate, lineType_id));
    }

    private List<PrepareSchedule> getData(DateTime startDate, Integer[] lineType_id) {
        List<LineType> lineTypes = lineTypeService.findByPrimaryKeys(lineType_id);
        List<PrepareSchedule> schedules = psService.findByLineTypeAndDate(lineTypes, startDate);
        if (!schedules.isEmpty()) {
            Map<String, MesPassStationInfo> dataMap = new HashMap<>();
            StationInfo stationInfo = stationMap.stream()
                    .filter(s -> s.lineTypeIds.containsAll(Arrays.asList(lineType_id)))
                    .findFirst().orElse(null);
            if (stationInfo == null) {
                return newArrayList();
            }
            List<Station> stations = stationInfo.stations;
            List<String> po = schedules.stream()
                    .map(PrepareSchedule::getPo)
                    .collect(Collectors.toList());
            List<MesPassStationInfo> passStationInfos = sqlViewService.findMesPassStationInfo(po, startDate);
            List<MesChangeTimeInfo> changeTimeInfos = sqlViewService.findMesChangeTimeDetail(po);

            schedules.stream().forEach(p -> {
                stations.forEach((station) -> {
                    MesPassStationInfo info = passStationInfos.stream()
                            .filter(i -> i.getWIP_NO().equals(p.getPo()) && i.getSTATION_ID().equals(new BigDecimal(station.token())))
                            .findFirst().orElse(null);
                    dataMap.put(station.name(), info);
                });

                Map<Object, Object> otherInfo = new HashMap<>();
                dataMap.forEach((k, v) -> {
                    int passQty = v == null ? 0 : v.getPASS_QTY().intValue();
                    int totalPassQty = v == null ? 0 : v.getTOTAL_PASS_QTY().intValue();
                    otherInfo.put("passCntQry_" + k, passQty);
                    otherInfo.put("totalPassCntQry_" + k, totalPassQty);
                });

                MesChangeTimeInfo changeTimeInfo = changeTimeInfos.stream()
                        .filter(i -> i.getWIP_NO().equals(p.getPo()) && i.getUNIT_NO().equals(stationInfo.section.getCode()))
                        .findFirst()
                        .orElse(null);

                otherInfo.put("changeTimeInfo", changeTimeInfo);

                p.setOtherInfo(otherInfo);

            });
        }
        return schedules;
    }

    private enum Station implements Encodeable {
        PREASSY(162), ASSY(2), T1(3), BI(4), T2(11), T3(30), T4(151), PACKAGE(28);

        private final Integer value;

        private Station(Integer value) {
            this.value = value;
        }

        @Override
        public Integer token() {
            return this.value;
        }
    }

    @RequestMapping(value = "/updateMemo", method = {RequestMethod.POST})
    @ResponseBody
    protected String updateMemo(
            @RequestParam int prepareSchedule_id,
            @RequestParam String memo,
            HttpServletRequest request
    ) {
        PrepareSchedule ps = wService.findByPrimaryKey(prepareSchedule_id);
        ps.setMemo(memo);
        wService.update(ps);
        return "success";
    }

    @RequestMapping(value = "/findPrepareScheduleRemark", method = {RequestMethod.GET})
    @ResponseBody
    protected DataTableResponse findPrepareScheduleRemark(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") DateTime startDate,
            @RequestParam("lineType_id[]") Integer[] lineType_id,
            HttpServletRequest request
    ) {
        return new DataTableResponse(psRemarkService.findByLineTypeAndDate(lineType_id, startDate));
    }

}
