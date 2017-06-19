/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * 
 */
package com.advantech.servlet;

import com.advantech.entity.Cell;
import com.advantech.entity.CellLine;
import com.advantech.helper.CronTrigMod;
import com.advantech.helper.DatetimeGenerator;
import com.advantech.helper.ParamChecker;
import com.advantech.quartzJob.CellStation;
import com.advantech.service.BasicService;
import com.advantech.service.CellLineService;
import com.advantech.service.CellService;
import java.io.*;
import static java.lang.System.out;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import org.json.JSONArray;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

/**
 *
 * @author Wei.Cheng 前端給予PO, servlet 負責 sched the new polling job.
 */
@WebServlet(name = "CellScheduleJobServlet", urlPatterns = {"/CellScheduleJobServlet"})
public class CellScheduleJobServlet extends HttpServlet {

    private String jobName = null;
    private final String jobGroup = "Cell";
    private final String cronTrig = "0 0/1 8-20 ? * MON-SAT *";
    private CronTrigMod ctm = null;
    private ParamChecker pc = null;
    private DatetimeGenerator dg = null;
    private Map<String, JobKey> schedJobs;
    private CellService cellService;
    private CellLineService cellLineService;

    @Override
    public void init() throws ServletException {
        ctm = CronTrigMod.getInstance();
        pc = new ParamChecker();
        dg = new DatetimeGenerator("yy-MM-dd");
        schedJobs = new HashMap();
        cellService = BasicService.getCellService();
        cellLineService = BasicService.getCellLineService();

        this.initProcessingCells();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        res.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");

        Object responseObject;

        switch (action) {
            case "insert":
                res.setContentType("text/plain");
                try {
                    String PO = req.getParameter("PO");
                    String modelname = req.getParameter("modelname");
                    String lineId = req.getParameter("lineId");
                    if (pc.checkInputVals(PO, lineId) == true) {

                        int line = Integer.parseInt(lineId);

                        CellLine cellLine = cellLineService.findOne(line);

                        if (cellLine.isOpened()) {
                            if (cellService.getCellProcessing(line).isEmpty()) {
//                                if (BasicService.getBabService().getPoTotalQuantity(PO) != null) {
                                if (cellService.insert(new Cell(line, PO, modelname))) {
                                    responseObject = this.schedNewJobs(line, PO) ? "success" : "fail";
                                } else {
                                    responseObject = "fail";
                                }
//                                } else {
//                                    responseObject = "PO is not exist";
//                                }
                            } else {
                                responseObject = "Some PO in this line is already processing";
                            }
                        } else {
                            responseObject = "This line is not opened, data insert fail";
                        }
                    } else {
                        responseObject = "Invalid input values";
                    }

                } catch (SchedulerException ex) {
                    responseObject = "fail";
                }
                break;
            case "select":
                res.setContentType("text/plain");
                try {
                    List l = getSchedJobs();
                    if (l.isEmpty()) {
                        responseObject = "No jobKey exist";
                    } else {
                        res.setContentType("application/json");
                        responseObject = new JSONArray(l);
                    }
                } catch (SchedulerException ex) {
                    responseObject = "fail";
                }
                break;
            case "delete":
                res.setContentType("text/plain");
                String lineId = req.getParameter("lineId");
                String PO = req.getParameter("PO");
                if (pc.checkInputVals(PO, lineId) == true) {
                    try {
                        int line = Integer.parseInt(lineId);
                        List<Cell> cells = cellService.getCellProcessing(line);
                        if (!cells.isEmpty()) {
                            Cell cell = (Cell) cells.get(0);
                            CellLine cellLine = BasicService.getCellLineService().findOne(cell.getLineId());
                            CellStation.checkDifferenceAndInsert(PO, cellLine.getAps_lineId()); //Don't forget to check the xml data and insert at last.
                            if (cellService.delete(cell) == true) {
                                responseObject = removeJob(line, PO) ? "success" : "fail";
                            } else {
                                responseObject = "fail";
                            }
                        } else {
                            responseObject = "fail";
                        }
                    } catch (SchedulerException ex) {
                        responseObject = "fail";
                    }
                } else {
                    responseObject = "Invalid input values";
                }
                break;
            default:
                res.setContentType("text/plain");
                responseObject = "Unsupport action.";
                break;
        }
        res.getWriter().print(responseObject);
    }

    private void initProcessingCells() {
        List<Cell> l = cellService.getCellProcessing();
        for (Cell cell : l) {
            try {
                this.schedNewJobs(cell.getLineId(), cell.getPO());
            } catch (SchedulerException ex) {
                out.println(ex);
            }
        }
    }

    private List<JobKey> getSchedJobs() throws SchedulerException {
        return ctm.getJobKeys("Cell");
    }

    private boolean schedNewJobs(int lineId, String PO) throws SchedulerException {
        CellLine cellLine = BasicService.getCellLineService().findOne(lineId);
        jobName = cellLine.getName() + "_" + cellLine.getAps_lineId() + "_" + PO;
        Map data = new HashMap();
        data.put("PO", PO);
        data.put("lineId", cellLine.getId());
        data.put("apsLineId", cellLine.getAps_lineId());
        data.put("today", dg.getToday());
        JobKey jobKey = ctm.createJobKey(jobName, jobGroup);
        TriggerKey triggerKey = ctm.createTriggerKey(jobName, jobGroup);
        JobDetail detail = ctm.createJobDetail(jobKey, jobGroup, CellStation.class, data);
        ctm.scheduleJob(detail, triggerKey, cronTrig);
        schedJobs.put(Integer.toString(cellLine.getId()), jobKey);
        return true;
    }

    private boolean removeJob(int lineId, String PO) throws SchedulerException {
        CellLine cellLine = BasicService.getCellLineService().findOne(lineId);
        if (cellLine == null) {
            return false;
        }
//        String key = cellLine.getName() + "_" + cellLine.getAps_lineId() + "_" + PO;
        String key = Integer.toString(lineId);
        JobKey jobKey = schedJobs.get(key);
        if (jobKey == null) {
            jobKey = ctm.createJobKey(cellLine.getName() + "_" + cellLine.getAps_lineId() + "_" + PO, this.jobGroup);
        }
        if (ctm.isJobInScheduleExist(jobKey)) {
            ctm.removeJob(jobKey);
            return true;
        } else {
            return false;
        }
    }
}
