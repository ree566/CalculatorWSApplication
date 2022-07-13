/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import static com.advantech.helper.ShiftScheduleUtils.*;
import java.util.HashMap;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author MFG.ESOP
 */
@Controller
@RequestMapping(value = "/UserShiftController")
public class UserShiftController {

    @RequestMapping(value = "/findDateShiftInfo", method = {RequestMethod.GET})
    @ResponseBody
    public Map findDateShiftInfo() {
        Shift shift = getShift();

//        DateTime sd = getCurrentShiftStart(), ed = getCurrentShiftEnd();

        //暫時設定shift end time 固定在早上8點
        DateTime sd = DateTime.now(), ed = (shift == Shift.MORNING_SHIFT ? new DateTime(sd).plusDays(1) : new DateTime(sd));
        ed = ed.withTime(8, 0, 0, 0);

        Map m = new HashMap();
        m.put("SHIFT_TYPE", shift);
        m.put("SHIFT_START", sd);
        m.put("SHIFT_END", ed);
        return m;
    }

}
