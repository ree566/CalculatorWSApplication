/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.model.db1.BabSettingHistory;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Line;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.LineUserReference;
import com.advantech.model.db1.PrepareSchedule;
import com.advantech.model.db1.User;
import com.advantech.service.db1.BabSettingHistoryService;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.LineService;
import com.advantech.service.db1.LineTypeService;
import com.advantech.service.db1.LineUserReferenceService;
import com.advantech.service.db1.PrepareScheduleService;
import static com.google.common.collect.Lists.newArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import static java.util.Comparator.comparing;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.stream.IntStream;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Component
@Transactional
public abstract class ArrangePrepareSchedule {

    private static final Logger logger = LoggerFactory.getLogger(ArrangePrepareSchedule.class);

    @Autowired
    private FloorService floorService;
    
    @Autowired
    private PrepareScheduleService psService;

    @Autowired
    private LineUserReferenceService lineUserRefService;

    @Autowired
    private LineService lineService;

    @Autowired
    private BabSettingHistoryService settingHistoryService;
    
    @Autowired
    private LineTypeService lineTypeService;

    private List<Interval> restTimes;
    private DateTime scheduleStartTime;
    private DateTime scheduleEndTime;

    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

    Line emptyLine;

    public void execute() throws Exception {
        this.execute(DateTime.now());
    }

    public void execute(DateTime d) throws Exception {
        d = d.withTime(0, 0, 0, 0);

        List<Floor> floors = floorService.findAll();
        floors = floors.stream()
                .filter(f -> f.getId() == 1 || f.getId() == 2)
                .collect(toList());

        for (Floor f : floors) {
            List<PrepareSchedule> ps = this.findPrepareSchedule(f, d);
            ps.forEach((p) -> {
                psService.update(p);
            });
        }

        logger.info("Update prepareSchedule finish");
    }

    public List<PrepareSchedule> findPrepareSchedule(Floor f, DateTime d) {
        /*
            Auto schedule line at 6:00 am
            Refresh schedule time & people when user change line schedule priority or user's setting
         */
 /*
            Get all user in line setting << how to group as Map<Line, List<LineUserReference>> result?
            Foreach schedule
            Find best user setting & last finished time not > 21:30
            Set schedule line
        
            Where to decide schedule order?
         */

        updateDateParamater(d);

        List<LineType> lt = lineTypeService.findByPrimaryKeys(1, 2);
        
        List<Line> totalLine = lineService.findBySitefloorAndLineType(f.getName(), lt);

        List<Line> lines = totalLine.stream().filter(l -> l.getLineType().getId() == 1).collect(toList());
        List<Line> cellLines = totalLine.stream().filter(l -> l.getLineType().getId() == 2).collect(toList());

        List<PrepareSchedule> l = psService.findByFloorAndLineTypeAndDate(f, lt, d);

        List<PrepareSchedule> noneCellableSchedules = l.stream()
                .filter(p -> p.getTimeCost().compareTo(new BigDecimal(100)) >= 0)
                .collect(toList());

        List<PrepareSchedule> cellableSchedules = l.stream()
                .filter(p -> p.getTimeCost().compareTo(new BigDecimal(100)) == -1)
                .collect(toList());

        noneCellableSchedules = noneCellableSchedules.stream()
                .sorted(comparing(PrepareSchedule::getTimeCost).reversed())
                .collect(toList());

        List<LineUserReference> users = lineUserRefService.findByLinesAndDate(lines, d);
        List<LineUserReference> cellUsers = lineUserRefService.findByLinesAndDate(cellLines, d);

        emptyLine = lineService.findByPrimaryKey(7);

        List<PrepareSchedule> result = new ArrayList();
        if (!noneCellableSchedules.isEmpty()) {
            result.addAll(setScheduleLine(users, noneCellableSchedules));
        }
        if (!cellableSchedules.isEmpty()) {
            result.addAll(setScheduleLine(cellUsers, cellableSchedules));
        }
        return result;
    }

    private void updateDateParamater(DateTime d) {
        scheduleStartTime = new DateTime(d).withTime(8, 30, 0, 0);
//        scheduleEndTime = new DateTime(d).withTime(21, 0, 0, 0);
        scheduleEndTime = new DateTime(d).withTime(20, 0, 0, 0);

        restTimes = newArrayList(
                new Interval(new DateTime(d).withTime(12, 0, 0, 0), new DateTime(d).withTime(12, 50, 0, 0)),
                new Interval(new DateTime(d).withTime(15, 30, 0, 0), new DateTime(d).withTime(15, 40, 0, 0)),
                new Interval(new DateTime(d).withTime(17, 30, 0, 0), new DateTime(d).withTime(18, 0, 0, 0))
        );

    }

    private List<PrepareSchedule> setScheduleLine(List<LineUserReference> users, List<PrepareSchedule> l) {

        //Find modelName fit in settings
        List<String> modelNames = l.stream().map(s -> s.getModelName()).collect(toList());
        List<BabSettingHistory> settings = settingHistoryService.findByBabModelNames(modelNames);

        //Find jobnumber fit in settings
//        List<BabSettingHistory> jSettings = settings.stream().filter(s -> {
//            LineUserReference fitSettings = users.stream()
//                    .filter(fw -> fw.getId().getUser().getJobnumber().equals(s.getJobnumber()) && fw.getStation() == s.getStation())
//                    .findFirst().orElse(null);
//            return fitSettings != null;
//        }).collect(toList());
        List<BabSettingHistory> jSettings = settings.stream().filter(s -> {
            LineUserReference fitSettings = users.stream()
                    .filter(fw -> fw.getUser().getJobnumber().equals(s.getJobnumber()))
                    .findFirst().orElse(null);
            return fitSettings != null;
        }).collect(toList());

        Map<Line, List<PrepareSchedule>> result = new HashMap();
        List<Line> lines = users.stream().map(u -> u.getLine()).distinct().collect(toList());
        lines = lines.stream().sorted(comparing(Line::getName)).collect(toList());

        //Add empty List into result
        lines.forEach((line) -> {
            result.put(line, new ArrayList());
        });

        result.put(emptyLine, new ArrayList());

        for (PrepareSchedule s : l) {
//            HibernateObjectPrinter.print(s);

            String modelName = s.getModelName();
            List<BabSettingHistory> modelNameFitSetting = jSettings.stream()
                    .filter(bsh -> bsh.getBab().getModelName().equals(modelName))
                    .collect(toList());

            //jSettings group by line & find max number of model experience
            //Map<Line, Integer cnt of users>
            Map<Line, List<Boolean>> historyFitUserSetting = users.stream()
                    .collect(groupingBy(x -> x.getLine(), mapping(f -> {
                        BabSettingHistory obj = modelNameFitSetting.stream()
                                .filter(bsh -> bsh.getJobnumber().equals(f.getUser().getJobnumber()))
                                .findFirst().orElse(null);
                        return obj != null;
                    }, toList())));

            //Check line to add schedule(must schedule time and before 21:30)
            findFitLineSetting(s, historyFitUserSetting, result, new ArrayList());
        }

        List<PrepareSchedule> result2 = new ArrayList();

        result.forEach((k, v) -> {
            int i = 1;
            for (PrepareSchedule p : v) {
                p.setPriority(i++);
            }

            List<User> settingUsers = users.stream().filter(u -> u.getLine().equals(k))
                    .map(u -> u.getUser())
                    .collect(toList());

            v.forEach(s -> {
                s.setUsers(settingUsers);
            });

            result2.addAll(v);
        });

        return result2;
    }

    private void findFitLineSetting(PrepareSchedule currentSchedule, Map<Line, List<Boolean>> lineUserSetting, Map<Line, List<PrepareSchedule>> result, List<Line> removeLine) {

        Map<Line, List<Boolean>> settingClone = new HashMap(lineUserSetting);

        removeLine.forEach((line) -> {
            settingClone.remove(line);
        });

        if (settingClone.isEmpty()) {
            currentSchedule.setLine(emptyLine);
            currentSchedule.setStartDate(null);
            currentSchedule.setEndDate(null);
            currentSchedule.setUsers(null);
            currentSchedule.setOtherInfo(null);
            result.get(emptyLine).add(currentSchedule);
            return;
        }

        //Find best line setting
        Line line = settingClone.entrySet()
                .stream()
                .max((Map.Entry<Line, List<Boolean>> e1, Map.Entry<Line, List<Boolean>> e2) -> {
                    int v1 = e1.getValue().stream().mapToInt(b -> b == true ? 1 : 0).sum();
                    int v2 = e2.getValue().stream().mapToInt(b -> b == true ? 1 : 0).sum();
                    int c1 = Integer.compare(v1, v2);
                    if (c1 != 0) {
                        return c1;
                    } else {
                        return e1.getKey().getName().compareTo(e2.getKey().getName());
                    }
                })
                .get()
                .getKey();

        if (result.isEmpty() || line == null) {
            return;
        }

        //Test and check last schedule time
        List<PrepareSchedule> lineSchedule = result.get(line);
        int usersCnt = lineUserSetting.get(line).size();
        List<PrepareSchedule> testScheduleList = newArrayList(lineSchedule);
        List<Boolean> fitStatus = lineUserSetting.get(line);
        Map<Integer, Boolean> fitStatusMap = IntStream.range(0, fitStatus.size())
                .boxed()
                .collect(toMap(i -> i, fitStatus::get));
        currentSchedule.setLine(line);
        currentSchedule.setOtherInfo(fitStatusMap);
        testScheduleList.add(currentSchedule);
        testScheduleList = scheduleTime(usersCnt, testScheduleList);

        //If last schedule finish time is after 21:30, remove line choose in map
        Date lastScheduleDate = testScheduleList.get(testScheduleList.size() - 1).getEndDate();
        if (new DateTime(lastScheduleDate).isAfter(scheduleEndTime)) {
            removeLine.add(line);
            findFitLineSetting(currentSchedule, lineUserSetting, result, removeLine);
        } else {
            result.replace(line, testScheduleList);
        }
    }

    private List<PrepareSchedule> scheduleTime(int users, List<PrepareSchedule> l) {

        int maxPeopleCnt = users;

        PrepareSchedule prev = null;
        int cnt = 0;

        for (PrepareSchedule w : l) {
            DateTime start = new DateTime(scheduleStartTime);
            if (cnt != 0 && prev != null) {
                start = new DateTime(prev.getEndDate());
            }
            BigDecimal addTime = w.getTimeCost().setScale(0, RoundingMode.UP);
            DateTime end = start.plusMinutes(addTime.divide(new BigDecimal(maxPeopleCnt), 0, BigDecimal.ROUND_UP).intValue());

            Interval timeAdjust = byPassRestTime(new Interval(start, end));

            w.setStartDate(timeAdjust.getStart().toDate());
            w.setEndDate(timeAdjust.getEnd().toDate());

            prev = w;
            cnt++;
        }
        return l;
    }

    private Interval byPassRestTime(Interval i) {
        for (Interval restTime : restTimes) {
            int iMin = Minutes.minutesBetween(i.getStart(), i.getEnd()).getMinutes();
            int restMin = Minutes.minutesBetween(restTime.getStart(), restTime.getEnd()).getMinutes();
            if (hasOverlap(i, restTime)) {
                if (isInRestTime(restTime, i.getStart()) && isInRestTime(restTime, i.getEnd())) {
                    /*
                        i   |----|
                        r |--------|   
                    */
                    return new Interval(restTime.getEnd(), restTime.getEnd().plusMinutes(iMin));
                } else if (isInRestTime(restTime, i.getStart()) && !isInRestTime(restTime, i.getEnd())) {
                    /*
                        i    |--------|
                        r |----|   
                    */
                    int overlap = Minutes.minutesBetween(i.getStart(), restTime.getEnd()).getMinutes();
                    return new Interval(restTime.getEnd(), i.getEnd().plusMinutes(overlap));
                } else if (!isInRestTime(restTime, i.getStart()) && isInRestTime(restTime, i.getEnd())) {
                    /*
                        i |----|
                        r   |--------|   
                    */
                    int overlap = Minutes.minutesBetween(restTime.getStart(), i.getEnd()).getMinutes();
                    return new Interval(i.getStart(), restTime.getEnd().plusMinutes(overlap));
                } else {
                    /*
                        i |--------|
                        r   |----|   
                    */
                    return new Interval(i.getStart(), i.getEnd().plusMinutes(restMin));
                }
            }
        }
        return i;
    }

    private boolean hasOverlap(Interval t1, Interval t2) {
        return !t1.getEnd().isBefore(t2.getStart()) && !t1.getStart().isAfter(t2.getEnd());
    }

    private boolean isInRestTime(Interval rest, DateTime d) {
        return rest.getStart().compareTo(d) * d.compareTo(rest.getEnd()) >= 0;
    }

}
