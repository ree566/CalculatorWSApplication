/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.dao.db1.LineUserReferenceDAO;
import com.advantech.model.db1.LineUserReference;
import static com.google.common.collect.Lists.newArrayList;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.PostConstruct;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Component
@Transactional
public class SyncLineUserReference extends PrepareScheduleJob {

    private static final Logger logger = LoggerFactory.getLogger(SyncLineUserReference.class);

    @Autowired
    private LineUserReferenceDAO dao;

    @PostConstruct
    public void init() {
        super.setExecuteDayCnt(1);
    }

    /*
        1. Fill data in line_user_ref2 when data in spec day is empty(prevent aps job execute some exception)
        2. Update yesterday's data to current date
     */
    @Override
    public void execute(List<DateTime> dts) throws Exception {

        dts.forEach(d -> {
            System.out.println(df.print(d));
        });

        /*
            後天要抓明天資料, 假如明天沒資料, 拷貝今天到明後天
            假如後天有資料, 照樣覆蓋明天資料到後天
         */
        for (DateTime d : dts) {
            d = d.withTime(0, 0, 0, 0);
            DateTime d2 = d.plusDays(1);
            if (d2.getDayOfWeek() == 6) {
                d2 = d2.plusDays(1);
            }
            DateTime d3 = d2.plusDays(1);
            if (d3.getDayOfWeek() == 6) {
                d3 = d2.plusDays(1);
            }

            final DateTime dd1 = d, dd2 = d2, dd3 = d3;

            List<LineUserReference> refs = dao.findByDate(newArrayList(d, d2, d3));
            List<LineUserReference> todayRefs = refs.stream().filter(lur -> new DateTime(lur.getOnboardDate()).equals(dd1)).collect(toList());
            List<LineUserReference> tomorrowRefs = refs.stream().filter(lur -> new DateTime(lur.getOnboardDate()).equals(dd2)).collect(toList());
            List<LineUserReference> dayAfterTomorrowRefs = refs.stream().filter(lur -> new DateTime(lur.getOnboardDate()).equals(dd3)).collect(toList());

            dao.delete(dayAfterTomorrowRefs);

            if (tomorrowRefs.isEmpty()) {
                List<LineUserReference> copyRefs = copyToDate(todayRefs, d2);
                List<LineUserReference> copyRefs2 = copyToDate(todayRefs, d3);
                copyRefs.addAll(copyRefs2);
                dao.insert(copyRefs);
            } else {
                List<LineUserReference> copyRefs = copyToDate(tomorrowRefs, d3);
                dao.insert(copyRefs);
            }
            logger.info("SyncLineUserReference data in next workday (" + super.df.print(d) + ") finish");
        }
    }

    private List<LineUserReference> copyToDate(List<LineUserReference> refs, DateTime d) {
        List<LineUserReference> l = new ArrayList();
        refs.forEach(ref -> {
            LineUserReference nr = new LineUserReference();

            nr.setLine(ref.getLine());
            nr.setStation(ref.getStation());
            nr.setUser(ref.getUser());
            nr.setOnboardDate(d.toDate());

            l.add(nr);
        });

        return l;
    }

}
