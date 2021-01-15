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
import com.advantech.model.db1.RptStationQty;
import com.advantech.quartzJob.ArrangePrepareScheduleImpl;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.LineTypeService;
import com.advantech.service.db1.PrepareScheduleService;
import com.advantech.webservice.Factory;
import com.advantech.webservice.WebServiceRV;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Lists.newArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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
    private WebServiceRV rv;

    private Map<List<Integer>, List<Station>> stationMap;

    @PostConstruct
    protected void init() {
        stationMap = ImmutableMap.<List<Integer>, List<Station>>builder()
                .put(newArrayList(9), newArrayList(Station.PREASSY))
                .put(newArrayList(1), newArrayList(Station.ASSY))
                .put(newArrayList(7), newArrayList(Station.T1, Station.BI))
                .put(newArrayList(8), newArrayList(Station.T2, Station.T3, Station.T4))
                .put(newArrayList(3), newArrayList(Station.PACKAGE))
                .build(); 
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
        Map<String, List<RptStationQty>> dataMap = new HashMap<>();
        List<Station> stations = stationMap.get(Arrays.asList(lineType_id));

        stations.forEach((station) -> {
            List<RptStationQty> mesQty = rv.getRptStationQtys(startDate, startDate.plusDays(1), station.token(), Factory.DEFAULT);
            dataMap.put(station.name(), mesQty);
        });

        schedules.stream().forEach(p -> {
            Map<Object, Object> otherInfo = new HashMap<>();
            dataMap.forEach((k, v) -> {
                int stationQty = v.stream().filter(m -> m.getPo().equals(p.getPo())).mapToInt(m -> m.getQty()).sum();
                otherInfo.put("passCntQry_" + k, stationQty);
            });
            p.setOtherInfo(otherInfo);

        });
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

}
