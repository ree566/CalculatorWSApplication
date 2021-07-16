/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabSettingHistory;
import com.advantech.model.db1.BabStatus;
import com.advantech.model.db1.SensorTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import static com.advantech.helper.ShiftScheduleUtils.*;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class BabSettingHistoryDAO extends AbstractDao<Integer, BabSettingHistory> implements BasicDAO_1<BabSettingHistory> {

    @Override
    public List<BabSettingHistory> findAll() {
        return super.createEntityCriteria().list();
    }

    public List<Map> findAll(String po, Integer line_id,
            boolean findWithBalance, boolean findWithMininumAlarmPercent, Integer minPcs) {
        String sql = "";

        if (findWithBalance) {
            sql = "select new Map(bsh as babAlarmHistory, bah.balance as balance, bah.totalPcs as totalPcs) from Bab b "
                    + " join b.babAlarmHistorys bah "
                    + " join b.babSettingHistorys bsh"
                    + " where b.po = :po"
                    + " and (:line_id is null or :line_id = 0 or b.line.id = :line_id)"
                    + " and bah.balance = ("
                    + " select max(bah2.balance) from BabAlarmHistory bah2 "
                    + " join bah2.bab b2 "
                    + " where b2.po = :po and "
                    + " (:line_id is null or :line_id = 0 or b2.line.id = :line_id)"
                    + " and bah2.totalPcs >= :minPcs)"
                    + " and bah.totalPcs >= :minPcs";
        } else if (findWithMininumAlarmPercent) {
            sql = "select new Map(bsh as babAlarmHistory, bah.failPcs * 1.0 / bah.totalPcs as alarmPercent, bah.totalPcs as totalPcs) from Bab b "
                    + " join b.babAlarmHistorys bah "
                    + " join b.babSettingHistorys bsh"
                    + " where b.po = :po and (:line_id is null or :line_id = 0 or b.line.id = :line_id)"
                    + " and (bah.failPcs * 1.0 / bah.totalPcs) = ("
                    + " select min(bah2.failPcs * 1.0 / bah2.totalPcs) from BabAlarmHistory bah2 "
                    + " join bah2.bab b2 where b2.po = :po"
                    + " and (:line_id is null or :line_id = 0 or b2.line.id = :line_id)"
                    + " and bah2.totalPcs >= :minPcs))"
                    + " and bah.totalPcs >= :minPcs";
        }

        if (!findWithBalance && !findWithMininumAlarmPercent) {
            return new ArrayList();
        }

        return super.getSession().createQuery(sql)
                .setParameter("po", po)
                .setParameter("line_id", line_id)
                .setParameter("minPcs", minPcs)
                .list();
    }

    @Override
    public BabSettingHistory findByPrimaryKey(Object obj_id) {
        return super.getByKey((int) obj_id);
    }

    public List<BabSettingHistory> findByBab(Bab b) {
        return super.createEntityCriteria()
                .add(Restrictions.eq("bab.id", b.getId()))
                .list();
    }

    public BabSettingHistory findByBabAndStation(Bab b, int station) {
        return (BabSettingHistory) super.createEntityCriteria()
                .add(Restrictions.eq("bab.id", b.getId()))
                .add(Restrictions.eq("station", station))
                .uniqueResult();
    }

    public List<BabSettingHistory> findProcessing() {
        return super.createEntityCriteria()
                .add(Restrictions.isNull("lastUpdateTime"))
                .list();
    }

    public BabSettingHistory findProcessingByTagName(SensorTransform tagName) {
        DateTime sD = getCurrentShiftStart();

        return (BabSettingHistory) super.createEntityCriteria()
                .add(Restrictions.eq("tagName", tagName))
                .add(Restrictions.isNull("lastUpdateTime"))
                .add(Restrictions.gt("createTime", sD.toDate()))
                .setMaxResults(1)
                .uniqueResult();
    }

    //Find the mininum bab_id per tagName
    public List<BabSettingHistory> findProcessingByLine(String lineName) {
        return super.getSession().createQuery(
                "from BabSettingHistory bsh join bsh.bab b join b.line l "
                + "where bsh.id in( "
                + "select min(bsh1.id) from BabSettingHistory bsh1 "
                + "join bsh1.bab b2 join b2.line l2 "
                + "where b2.babStatus is null "
                + ("CELL".equals(lineName)
                ? "and upper(l2.name) like CONCAT(upper(:lineName), '%')"
                : "and l2.name = :lineName ")
                + "and bsh1.lastUpdateTime is null "
                + "group by bsh1.tagName) "
                + "order by bsh.tagName")
                .setParameter("lineName", lineName)
                .list();
    }

    public List<BabSettingHistory> findProcessingByLine(int line_id) {
        DateTime sD = getCurrentShiftStart();
        
        return super.createEntityCriteria()
                .createAlias("bab", "b")
                .createAlias("b.line", "l")
                .createAlias("l.lineType", "lt")
                .add(Restrictions.isNull("b.babStatus"))
                .add(Restrictions.gt("b.beginTime", sD.toDate()))
                .add(Restrictions.eq("l.id", line_id))
                .addOrder(Order.asc("b.id"))
                .list();
    }

    public List<BabSettingHistory> findByBabModelNames(List<String> modelNames) {
        return super.createEntityCriteria()
                .createAlias("bab", "b")
                .add(Restrictions.in("b.modelName", modelNames))
                .add(Restrictions.eq("b.ispre", 0))
                .add(Restrictions.eq("b.babStatus", BabStatus.CLOSED))
                .list();
    }

    @Override
    public int insert(BabSettingHistory pojo) {
        super.getSession().save(pojo);
        return 1;
    }

    @Override
    public int update(BabSettingHistory pojo) {
        super.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(BabSettingHistory pojo) {
        super.getSession().delete(pojo);
        return 1;
    }

}
