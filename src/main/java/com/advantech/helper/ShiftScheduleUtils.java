/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 *
 * @author MFG.ESOP
 */
public class ShiftScheduleUtils {

    public enum Shift {
        MORNING_SHIFT, NIGHT_SHIFT, UNSUPPORT_SHIFT;
    }

    private static final LocalTime MORNING_SHIFT_STARTTIME = new LocalTime(8, 30, 0);
    private static final LocalTime MORNING_SHIFT_ENDTIME = new LocalTime(19, 45, 0);
    private static final LocalTime NIGHT_SHIFT_STARTTIME = new LocalTime(20, 30, 0);
    private static final LocalTime NIGHT_SHIFT_ENDTIME = new LocalTime(7, 45, 0);

    public static DateTime getMorningShiftStart() {
        return getMorningShiftStart(DateTime.now());
    }

    public static DateTime getMorningShiftStart(DateTime dt) {
        return new DateTime(dt).withTime(MORNING_SHIFT_STARTTIME);
    }

    public static DateTime getMorningShiftEnd() {
        return getMorningShiftEnd(DateTime.now());
    }

    public static DateTime getMorningShiftEnd(DateTime dt) {
        return new DateTime(dt).withTime(MORNING_SHIFT_ENDTIME);
    }

    public static DateTime getNightShiftStart() {
        return getNightShiftStart(DateTime.now());
    }

    public static DateTime getNightShiftStart(DateTime dt) {
        if (isCrossDay(dt)) {
            return new DateTime(dt).minusDays(1).withTime(NIGHT_SHIFT_STARTTIME);
        } else {
            return new DateTime(dt).withTime(NIGHT_SHIFT_STARTTIME);
        }
    }

    public static DateTime getNightShiftEnd() {
        return getNightShiftEnd(DateTime.now());
    }

    public static DateTime getNightShiftEnd(DateTime dt) {
        if (isCrossDay(dt)) {
            return new DateTime(dt).withTime(NIGHT_SHIFT_ENDTIME);
        } else {
            return new DateTime(dt).plusDays(1).withTime(NIGHT_SHIFT_ENDTIME);
        }
    }

    public static Shift getShift() {
        return getShift(DateTime.now());
    }

    public static Shift getShift(DateTime dt) {
        if (dt.isAfter(getMorningShiftStart(dt)) && dt.isBefore(getMorningShiftEnd(dt))) {
            return Shift.MORNING_SHIFT;
        } else if (dt.isAfter(getNightShiftStart(dt)) && dt.isBefore(getNightShiftEnd(dt))) {
            return Shift.NIGHT_SHIFT;
        }
        return Shift.UNSUPPORT_SHIFT;
    }

    private static boolean isCrossDay(DateTime dt) {
        return dt.isAfter(new DateTime(dt).withTime(0, 0, 0, 0)) && dt.isBefore(getMorningShiftStart(dt));
    }
}
