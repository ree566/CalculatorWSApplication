/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.Line;
import com.advantech.model.db1.LineUserReference;
import java.util.Date;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class LineUserReferenceDAO extends AbstractDao<Integer, LineUserReference> implements BasicDAO_1<LineUserReference> {

    @Override
    public List<LineUserReference> findAll() {
        return super.createEntityCriteria().list();
    }

    @Override
    public LineUserReference findByPrimaryKey(Object obj_id) {
        return super.getByKey((Integer) obj_id);
    }

    public List<LineUserReference> findByLine(Line line) {
        return super.createEntityCriteria()
                .add(Restrictions.eq("line", line))
                .addOrder(Order.asc("station"))
                .list();
    }

    public List<LineUserReference> findByLines(List<Line> line) {
        return super.createEntityCriteria()
                .add(Restrictions.in("line", line))
                .addOrder(Order.asc("station"))
                .list();
    }

    public List<LineUserReference> findByLineAndDate(Line line, DateTime d) {
        return super.createEntityCriteria()
                .add(Restrictions.eq("line", line))
                .add(Restrictions.eq("onboardDate", d.toDate()))
                .addOrder(Order.asc("station"))
                .list();
    }

    public List<LineUserReference> findByLinesAndDate(List<Line> line, DateTime d) {
        return super.createEntityCriteria()
                .add(Restrictions.in("line", line))
                .add(Restrictions.eq("onboardDate", d.toDate()))
                .addOrder(Order.asc("station"))
                .list();
    }

    public List<LineUserReference> findByDate(DateTime d) {
        return super.createEntityCriteria()
                .add(Restrictions.eq("onboardDate", d.toDate()))
                .list();
    }

    public List<LineUserReference> findByDate(List<DateTime> d) {
        List<Date> td = d.stream().map(dt -> dt.toDate()).collect(toList());
        return super.createEntityCriteria()
                .add(Restrictions.in("onboardDate", td))
                .list();
    }

    @Override
    public int insert(LineUserReference pojo) {
        super.getSession().save(pojo);
        return 1;
    }

    public int insert(List<LineUserReference> l) {
        for (LineUserReference lf : l) {
            this.insert(lf);
        }
        return 1;
    }

    @Override
    public int update(LineUserReference pojo) {
        super.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(LineUserReference pojo) {
        super.getSession().delete(pojo);
        return 1;
    }

    public int delete(List<LineUserReference> l) {
        for (LineUserReference lf : l) {
            this.delete(lf);
        }
        return 1;
    }

}
