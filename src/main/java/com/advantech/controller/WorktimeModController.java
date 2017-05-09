/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.controller;

import com.advantech.model.Unit;
import com.advantech.model.User;
import com.advantech.model.Worktime;
import com.advantech.model.WorktimeColumnGroup;
import com.advantech.service.WorktimeColumnGroupService;
import com.advantech.service.WorktimeService;
import com.google.common.base.Splitter;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
@Secured({"ROLE_USER", "ROLE_ADMIN"})
public class WorktimeModController {

    private final String ADD = "add", EDIT = "edit", DELETE = "del";
    private String userOper;
    private final String SUCCESS_MESSAGE = "SUCCESS";
    private final String FAIL_MESSAGE = "FAIL";

    @Autowired
    private WorktimeService worktimeService;

    @Autowired
    private WorktimeColumnGroupService worktimeColumnGroupService;

    //The request param name indexOf('name') == 1 is selected box which has one to many relationship.
    @RequestMapping(value = "/updateSheet.do", method = {RequestMethod.POST})
    public ResponseEntity updateSheet(
            @RequestParam String oper,
            @ModelAttribute Worktime worktime,
            HttpServletRequest req,
            BindingResult errors) throws ServletException, IOException {

        if (errors.hasErrors()) {
            // error handling code goes here.
            System.out.println(errors.getFieldErrors());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(errors.getFieldErrors());
        }

        this.printModels(worktime);
        this.showParams(req);

        String modifyMessage;

        userOper = oper;

        if (oper.equals(DELETE)) {
            Worktime existWorktime = worktimeService.findByPrimaryKey(worktime.getId());
            modifyMessage = worktimeService.delete(existWorktime) == 1 ? this.SUCCESS_MESSAGE : this.FAIL_MESSAGE;
        } else {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Unit userType = user.getUnit();
            if (userType != null) {
                if (isModelExists(worktime)) {
                    modifyMessage = "This model name is already exists";
                } else {
                    //Nullable select columns.
                    if (worktime.getPreAssy().getId() == 0) {
                        worktime.setPreAssy(null);
                    }

                    if (worktime.getFlowByBabFlowId().getId() == 0) {
                        worktime.setFlowByBabFlowId(null);
                    }

                    if (worktime.getFlowByTestFlowId().getId() == 0) {
                        worktime.setFlowByTestFlowId(null);
                    }

                    if (worktime.getFlowByPackingFlowId().getId() == 0) {
                        worktime.setFlowByPackingFlowId(null);
                    }

                    modifyMessage = this.updateRows(worktime);
                }
            } else {
                modifyMessage = "Unsupport unit";
            }
        }
        return ResponseEntity
                .status(SUCCESS_MESSAGE.equals(modifyMessage) ? HttpStatus.CREATED : HttpStatus.FORBIDDEN)
                .body(modifyMessage);
    }

    private String deleteRows(String rowId) {
        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
        List<String> ids = splitter.splitToList(rowId);
        Integer[] id = new Integer[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            id[i] = Integer.valueOf(ids.get(i));
        }
        List<Worktime> l = worktimeService.findByPrimaryKeys(id);
        worktimeService.delete(l);
        return this.SUCCESS_MESSAGE;
    }

    private String updateRows(Worktime worktime) {
        switch (userOper) {
            case ADD:
                return worktimeService.insert(worktime) == 1 ? this.SUCCESS_MESSAGE : FAIL_MESSAGE;
            case EDIT:
                return worktimeService.update(worktime) == 1 ? this.SUCCESS_MESSAGE : FAIL_MESSAGE;
            default:
                return "Unsupport action";
        }
    }

    private boolean isModelExists(Worktime worktime) {
        Worktime existWorktime = worktimeService.findByModel(worktime.getModelName());
        if (worktime.getId() == 0) {
            return existWorktime != null;
        } else {
            return existWorktime != null && existWorktime.getId() != worktime.getId();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/unitColumn.do", method = {RequestMethod.GET})
    public String[] getUnitColumnName() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int unit = user.getUnit().getId();

        String[] columnName;

        WorktimeColumnGroup w = worktimeColumnGroupService.findByUserType(unit);

        try {
            Clob columns = w.getColumnName();
            if (columns == null) {
                return new String[0];
            } else {
                String clobString = columns.getSubString(1, (int) columns.length());
                columnName = clobString.split(",");
                return columnName;
            }
        } catch (SQLException ex) {
            return new String[0];
        }
    }

    private void printModels(Object... model) {
        System.out.println(new Gson().toJson(model));
    }

    private void showParams(HttpServletRequest req) throws ServletException, IOException {
        Enumeration params = req.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = (String) params.nextElement();
            System.out.println("Parameter Name - " + paramName + ", Value - " + req.getParameter(paramName));
        }
    }

}
