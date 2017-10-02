/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.controller;

import com.advantech.service.WorktimeUploadMesService;
import com.advantech.helper.WorktimeMailManager;
import static com.advantech.helper.JqGridResponseUtils.toJqGridResponse;
import com.advantech.jqgrid.PageInfo;
import com.advantech.model.Worktime;
import com.advantech.jqgrid.JqGridResponse;
import com.advantech.service.WorktimeService;
import static com.google.common.collect.Lists.newArrayList;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Wei.Cheng
 */
@Controller
@RequestMapping("/Worktime")
public class WorktimeController extends CrudController<Worktime> {

    @Autowired
    private WorktimeService worktimeService;

    @Autowired
    private WorktimeMailManager worktimeMailManager;

    @ResponseBody
    @RequestMapping(value = SELECT_URL, method = {RequestMethod.GET})
    @Override
    protected JqGridResponse read(PageInfo info) {
        return toJqGridResponse(worktimeService.findAll(info), info);
    }

    @RequestMapping(value = INSERT_URL, method = {RequestMethod.POST})
    @Override
    protected ResponseEntity insert(@Valid @ModelAttribute Worktime worktime, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return serverResponse(bindingResult.getFieldErrors());
        }

        String modifyMessage;

        worktimeService.checkModelExists(worktime);
        resetNullableColumn(worktime);
        modifyMessage = worktimeService.insertWithFormulaSetting(worktime) == 1 ? this.SUCCESS_MESSAGE : FAIL_MESSAGE;
        if (SUCCESS_MESSAGE.equals(modifyMessage)) {
            worktimeMailManager.notifyUser(newArrayList(worktime), ADD);
        }

        return serverResponse(modifyMessage);
    }

    @RequestMapping(value = UPDATE_URL, method = {RequestMethod.POST})
    @Override
    protected ResponseEntity update(@Valid @ModelAttribute Worktime worktime, BindingResult bindingResult) throws Exception {

        if (bindingResult.hasErrors()) {
            return serverResponse(bindingResult.getFieldErrors());
        }

        String modifyMessage;

        worktimeService.checkModelExists(worktime);
        resetNullableColumn(worktime);

        modifyMessage = worktimeService.merge(worktime) == 1 ? this.SUCCESS_MESSAGE : FAIL_MESSAGE;

        return serverResponse(modifyMessage);
    }

    @RequestMapping(value = DELETE_URL, method = {RequestMethod.POST})
    @Override
    protected ResponseEntity delete(int id) throws Exception {
        Worktime w = worktimeService.findByPrimaryKey(id);
        String modifyMessage = worktimeService.delete(id) == 1 ? this.SUCCESS_MESSAGE : this.FAIL_MESSAGE;
        if (SUCCESS_MESSAGE.equals(modifyMessage)) {
            worktimeMailManager.notifyUser(newArrayList(w), DELETE);
        }
        return serverResponse(modifyMessage);
    }

    private void resetNullableColumn(Worktime worktime) {
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

        if (worktime.getUserByEeOwnerId().getId() == 0) {
            worktime.setUserByEeOwnerId(null);
        }
        
    }

}
