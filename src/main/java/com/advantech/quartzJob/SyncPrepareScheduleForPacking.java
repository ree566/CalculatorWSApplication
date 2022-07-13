/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.model.db1.Floor;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.PrepareSchedule;
import com.advantech.model.db1.Worktime;
import com.advantech.model.view.db3.WorktimeCobots;
import com.advantech.service.db1.FloorService;
import com.advantech.service.db1.LineTypeService;
import com.advantech.service.db1.PrepareScheduleService;
import com.advantech.service.db1.WorktimeService;
import static com.google.common.collect.Lists.newArrayList;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Wei.Cheng For assy
 */
@Component
public class SyncPrepareScheduleForPacking {

    private static final Logger logger = LoggerFactory.getLogger(SyncPrepareScheduleForPacking.class);

    @Autowired
    private ArrangePrepareScheduleImpl_Packing aps;

    @Autowired
    private PrepareScheduleService psService;

    @Autowired
    private WorktimeService worktimeService;

    @Autowired
    private LineTypeService lineTypeService;

    @Autowired
    private FloorService floorService;
    
    @Autowired
    private com.advantech.service.db3.SqlViewService sqlViewService;

    public void execute() throws Exception {
        /*
            ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
            Because oom problem on poi, excel sync job set on c# winform project
            ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
        */
        //        this.execute(d);

        logger.info("Update packing prepareSchedule...");
        aps.execute();
    }

    public void execute(DateTime d) throws Exception {
        d = d.withTime(0, 0, 0, 0);

        //1 for floor 5, 2 for floor 6
        List<Floor> floors = floorService.findByPrimaryKeys(1, 2);

        List<Worktime> worktimes = worktimeService.findNotZeroPackingLeadTime();
        LineType pkg = lineTypeService.findByPrimaryKey(3);

        for (Floor f : floors) {
            String floorNumber = f.getName().replaceAll("^(\\d+).*$", "$1");
            String syncFilePath = "\\\\aclfile.advantech.corp\\Group1\\DF\\PMC\\生產日排程\\TWM3 " + floorNumber + "F APS製程排程.xlsx";

            List nextDaysData = psService.findByFloorAndLineTypeAndDate(f, newArrayList(pkg), d);
            if (!nextDaysData.isEmpty()) {
                //Bypass the data when data is already exists
                continue;
            }

            try ( Workbook workbook = WorkbookFactory.create(new File(syncFilePath), "234", true)) {
                Sheet sheet = workbook.getSheet(floorNumber + "F--包裝");

                int dateIdx = 5;
                int titleIdx = 6;

                int patchColumn = findColumnIdx(sheet.getRow(dateIdx), d);
                int modelNameIdx = findColumnIdx(sheet.getRow(titleIdx), "料號");
                int poIdx = findColumnIdx(sheet.getRow(titleIdx), "工單");
                int totalQtyIdx = findColumnIdx(sheet.getRow(titleIdx), "工單數");
                int scheduleQtyIdx = patchColumn;

                //Step 1: First get column index matches the current date
                //Iterate through each rows one by one
                int cnt = 0;

                Iterator<Row> rowIterator = sheet.iterator();
                List<PrepareSchedule> schedules = new ArrayList<>();
                while (rowIterator.hasNext()) {
                    try {
                        //Skip row to main data
                        if (cnt > titleIdx) {
                            Row row = rowIterator.next();
                            Cell cell_OutputCnt = row.getCell(patchColumn);
                            if (cell_OutputCnt != null && cell_OutputCnt.getCellType() != CellType.BLANK) {

                                Cell cell_ModelName = row.getCell(modelNameIdx);

                                String modelName = cell_ModelName.getStringCellValue();
                                Worktime w = worktimes.stream()
                                        .filter(o -> o.getModelName().equals(modelName))
                                        .findFirst()
                                        .orElse(null);

                                if (w != null) {

                                    Cell cell_Po = row.getCell(poIdx);
                                    Cell cell_TotalQty = row.getCell(totalQtyIdx);
                                    Cell cell_ScheduleQty = row.getCell(scheduleQtyIdx);

                                    logger.info(cell_ModelName.getStringCellValue());

                                    PrepareSchedule p = new PrepareSchedule();
                                    p.setModelName(modelName);
                                    p.setPo(cell_Po.getStringCellValue());
                                    p.setTotalQty((int) cell_TotalQty.getNumericCellValue());
                                    p.setScheduleQty((int) cell_ScheduleQty.getNumericCellValue());
                                    p.setTimeCost(w.getPackingLeadTime().multiply(new BigDecimal(p.getScheduleQty()))); //花費紀錄時間為附件盒工時
                                    p.setLineType(pkg);
                                    p.setOnBoardDate(d.toDate());
                                    p.setFloor(f);
                                    schedules.add(p);
                                }
                            }

                        }
                        cnt++;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                this.psService.insert(schedules);
                this.saveCobotsInfo(schedules);
            }
        }

        logger.info("SyncPrepareSchedule finish");
    }

    private int findColumnIdx(Row r, Object keyword) throws Exception {

        int patchColumn = -1;

        for (int cn = 0; cn < r.getLastCellNum(); cn++) {
            Cell c = r.getCell(cn);
            if (c == null || c.getCellType() == CellType.BLANK) {
                // Can't be this cell - it's empty
                continue;
            }
            if (c.getCellType() == CellType.NUMERIC) {
                if (keyword != null && keyword instanceof DateTime && HSSFDateUtil.isCellDateFormatted(c)) {
                    DateTime v = new DateTime(c.getDateCellValue());
                    if (((DateTime) keyword).isEqual(v)) {
                        patchColumn = cn;
                        break;
                    }
                }
            } else if (c.getCellType() == CellType.STRING) {
                if (Objects.equals(keyword, c.getStringCellValue())) {
                    patchColumn = cn;
                    break;
                }
            }
        }

        if (patchColumn == -1) {
            throw new Exception("None of the cells in the first row were Patch");
        }

        return patchColumn;
    }
    
    private void saveCobotsInfo(List<PrepareSchedule> prepareSchedules){
        List<String> modelNames = prepareSchedules
                .stream().map(p -> p.getModelName())
                .collect(toList());
        Map<String, WorktimeCobots> worktimeCobotSetting = this.sqlViewService
                .findCobots(modelNames).stream()
                .collect(Collectors.toMap(WorktimeCobots::getModelName, Function.identity()));
        prepareSchedules.forEach(p -> {
            if(worktimeCobotSetting.containsKey(p.getModelName())){
                p.setHrcMemo(worktimeCobotSetting.get(p.getModelName()).getCobots());
            }
        });
        this.psService.update(prepareSchedules);
    }
}
