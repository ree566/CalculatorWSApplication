/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import static com.google.common.collect.Lists.newArrayList;
import java.util.ArrayList;
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

    private int executeDayCnt = 4;

    public void execute() throws Exception {

        List<DateTime> executeDays = new ArrayList();

        DateTime d = new DateTime().withTime(0, 0, 0, 0);
        if (d.getHourOfDay() >= 17) {
            d = d.plusDays(d.getDayOfWeek() == 6 ? 2 : 1);
        }

        for (int i = 0; i < executeDayCnt; i++) {
            if (d.getDayOfWeek() == 7) {
                d = d.plusDays(1);
            }
            executeDays.add(d);
            d = d.plusDays(1);
        }

        this.execute(executeDays);
    }

    abstract void execute(List<DateTime> dts) throws Exception;

    protected void setExecuteDayCnt(int cnt) {
        this.executeDayCnt = cnt;
    }

}
