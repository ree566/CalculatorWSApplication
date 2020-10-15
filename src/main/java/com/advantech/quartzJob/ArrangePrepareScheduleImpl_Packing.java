/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.model.db1.Floor;
import com.advantech.model.db1.Line;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.PrepareSchedule;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.LineService;
import com.advantech.service.db1.LineTypeService;
import com.advantech.service.db1.PrepareScheduleService;
import static com.google.common.base.Preconditions.checkState;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 *
 */
@Component
@Transactional
public class ArrangePrepareScheduleImpl_Packing extends PrepareScheduleJob {

    private static final Logger logger = LoggerFactory.getLogger(ArrangePrepareScheduleImpl_Packing.class);

    @Autowired
    private FloorService floorService;

    @Autowired
    private PrepareScheduleService psService;

    @Autowired
    private LineService lineService;

    @Autowired
    private LineTypeService lineTypeService;

    @Override
    public void execute(List<DateTime> dts) throws Exception {
        dts.forEach(d -> {
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
        });
        logger.info("Update prepareSchedule finish");
    }

    public List<PrepareSchedule> findPrepareSchedule(Floor f, DateTime d) {

        List<LineType> lt = lineTypeService.findByPrimaryKeys(3);
        List<Line> lines = lineService.findBySitefloorAndLineType(f.getName(), lt);
        List<PrepareSchedule> schedules = psService.findByFloorAndLineTypeAndDate(f, lt, d);

        Line line = lines.stream().filter(l -> l.getLock() == 0).findFirst().orElse(null);

        checkState(line != null, "Can't find any unlock line in setting");
        
        schedules.forEach(s -> {
            s.setLine(line);
        });

        return schedules;
    }
}
