/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author wei.cheng
 */
@Component
@Transactional
public abstract class PrepareScheduleJob {

    protected DateTimeFormatter df = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss");

    public void execute() throws Exception {
        DateTime d = new DateTime();
        if (d.getHourOfDay() >= 17) {
            d = d.plusDays(d.getDayOfWeek() == 6 ? 2 : 1);
        }

        DateTime d2 = new DateTime(d.plusDays(1));
        if (d2.getDayOfWeek() == 7) {
            d2 = d2.plusDays(1);
        }

        DateTime d3 = new DateTime(d2.plusDays(1));
        if (d3.getDayOfWeek() == 7) {
            d3 = d3.plusDays(1);
        }

        this.execute(newArrayList(d, d2, d3));
    }

    abstract void execute(List<DateTime> dts) throws Exception;

}
