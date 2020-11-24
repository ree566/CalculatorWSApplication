/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.model.db1.Floor;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.PrepareSchedule;
import com.advantech.service.db1.PrepareScheduleService;
import static com.google.common.collect.Lists.newArrayList;
import java.io.File;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng For assy
 */
@Component
@Transactional
public class SyncPrepareScheduleForAssy {

    private static final Logger logger = LoggerFactory.getLogger(SyncPrepareScheduleForAssy.class);

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ArrangePrepareScheduleImpl_Assy aps;

    @Autowired
    private PrepareScheduleService psService;

    public void execute() throws Exception {
        //Because oom problem on poi, excel sync job set on c# winform project
//        this.execute(d);
        logger.info("Update assy prepareSchedule...");
        aps.execute();
    }

    public void execute(DateTime d) throws Exception {

        d = d.withTime(0, 0, 0, 0);
        Session session = sessionFactory.getCurrentSession();
        int[] floors = {5, 6};
        LineType assy = session.get(LineType.class, 1);

        for (int floor : floors) {

            String syncFilePath = "\\\\aclfile.advantech.corp\\Group1\\DF\\PMC\\生產日排程\\APS " + floor + "F 組裝排程.xlsx";

            Floor f = session.get(Floor.class, floor == 5 ? 1 : 2);

            List nextDaysData = psService.findByFloorAndLineTypeAndDate(f, newArrayList(assy), d);
            if (!nextDaysData.isEmpty()) {
                //Bypass the data when data is already exists
                continue;
            }

            try (Workbook workbook = WorkbookFactory.create(new File(syncFilePath), "234", true)) {
                Sheet sheet = workbook.getSheet(floor + "F--前置&組裝");

                int dateIdx = 5;
                int titleIdx = 6;

                int patchColumn = findColumnIdx(sheet.getRow(dateIdx), d);
                int lineTypeIdx = findColumnIdx(sheet.getRow(titleIdx), "料號&製程段");
                int modelNameIdx = findColumnIdx(sheet.getRow(titleIdx), "料號");
                int poIdx = findColumnIdx(sheet.getRow(titleIdx), "工單");
                int totalQtyIdx = findColumnIdx(sheet.getRow(titleIdx), "工單數");
                int scheduleQtyIdx = patchColumn;
                int timeCostIdx = patchColumn + 1;

                //Step 1: First get column index matches the current date
                //Step 2: Filter data B7 contain word "BASSY" & 排產量 is not blank
                //Iterate through each rows one by one
                int cnt = 0;

                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext()) {
                    try {
                        //Skip row to main data
                        if (cnt > titleIdx) {
                            Row row = rowIterator.next();
                            Cell cell_LineType = row.getCell(lineTypeIdx);
                            Cell cell_OutputCnt = row.getCell(patchColumn);
                            if (cell_LineType != null && cell_OutputCnt != null) {
                                cell_LineType.setCellType(CellType.STRING);
                                if (cell_LineType.getRichStringCellValue().toString().contains("BASSY") && cell_OutputCnt.getCellType() != CellType.BLANK) {

                                    Cell cell_ModelName = row.getCell(modelNameIdx);
                                    Cell cell_Po = row.getCell(poIdx);
                                    Cell cell_TotalQty = row.getCell(totalQtyIdx);
                                    Cell cell_ScheduleQty = row.getCell(scheduleQtyIdx);
                                    Cell cell_TimeCost = row.getCell(timeCostIdx);

                                    logger.info(cell_ModelName.getStringCellValue());

                                    PrepareSchedule p = new PrepareSchedule();
                                    p.setModelName(cell_ModelName.getStringCellValue());
                                    p.setPo(cell_Po.getStringCellValue());
                                    p.setTotalQty((int) cell_TotalQty.getNumericCellValue());
                                    p.setScheduleQty((int) cell_ScheduleQty.getNumericCellValue());
                                    p.setTimeCost(new BigDecimal(cell_TimeCost.getNumericCellValue()));
                                    p.setLineType(assy);
                                    p.setOnBoardDate(d.toDate());
                                    p.setFloor(f);
                                    session.save(p);
                                }
                            }
                        }
                        cnt++;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
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
}
