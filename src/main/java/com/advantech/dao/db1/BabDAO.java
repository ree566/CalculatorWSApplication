/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.Bab;
import com.advantech.model.db1.Line;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.ReplyStatus;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng bab資料表就是生產工單資料表
 */
@Repository
public class BabDAO extends AbstractDao<Integer, Bab> implements BasicDAO_1<Bab> {

    @Override
    public List<Bab> findAll() {
        return super.createEntityCriteria().list();
    }

    @Override
    public Bab findByPrimaryKey(Object obj_id) {
        return super.getByKey((int) obj_id);
    }

    public List<Bab> findByPrimaryKeys(Integer... obj_ids) {
        return super.createEntityCriteria()
                .add(Restrictions.in("id", obj_ids))
                .list();
    }

    public Bab findWithLineInfo(int bab_id) {
        return (Bab) super.createEntityCriteria()
                .add(Restrictions.eq("id", bab_id))
                .createAlias("line", "l")
                .createAlias("l.lineType", "lt")
                .uniqueResult();
    }

    public List<Bab> findByModelAndDate(String modelName, DateTime sD, DateTime eD) {
        sD = sD.withHourOfDay(0);
        eD = eD.withHourOfDay(23);
        return super.createEntityCriteria()
                .add(Restrictions.eq("modelName", modelName))
                .add(Restrictions.between("beginTime", sD.toDate(), eD.toDate()))
                .setFetchMode("line", FetchMode.JOIN)
                .list();
    }

    public List<Bab> findByDate(DateTime sD, DateTime eD) {
        sD = sD.withHourOfDay(0);
        eD = eD.withHourOfDay(23);
        return super.createEntityCriteria()
                .add(Restrictions.between("beginTime", sD.toDate(), eD.toDate()))
                .list();
    }

    public List<Bab> findByDateAndLineType(DateTime sD, DateTime eD, List<LineType> l) {
        sD = sD.withHourOfDay(0);
        eD = eD.withHourOfDay(23);
        return super.createEntityCriteria()
                .createAlias("line", "l")
                .add(Restrictions.between("beginTime", sD.toDate(), eD.toDate()))
                .add(Restrictions.in("l.lineType", l))
                .add(Restrictions.ne("l.id", 7))
                .list();
    }

    public List<Bab> findByMultipleClause(DateTime sD, DateTime eD, int lineType_id, int floor_id, boolean isAboveTenPcs) {
        sD = sD.withHourOfDay(0);
        eD = eD.withHourOfDay(23);
        Criteria c = super.createEntityCriteria();
        c.add(Restrictions.between("beginTime", sD.toDate(), eD.toDate()));
        c.createAlias("line", "l");
        if (lineType_id != -1) {
            c.add(Restrictions.eq("l.lineType.id", lineType_id));
        }
        if (floor_id != -1) {
            c.add(Restrictions.eq("l.floor.id", floor_id));
        }
        if (isAboveTenPcs) {
            c.createAlias("babAlarmHistorys", "bah");
            c.add(Restrictions.gt("bah.totalPcs", 10 - 1));
        }
        return c.list();
    }

    public List<Bab> findProcessing() {
        return super.createEntityCriteria()
                .createAlias("line", "l")
                .createAlias("l.lineType", "lineType")
                .add(Restrictions.isNull("babStatus"))
                .list();
    }

    public List<Bab> findProcessingAndNotPre() {
        return super.createEntityCriteria()
                .createAlias("line", "l")
                .createAlias("l.lineType", "lineType")
                .add(Restrictions.isNull("babStatus"))
                .add(Restrictions.eq("ispre", 0))
                .list();
    }

    public List<Bab> findProcessingByTagName(String tagName) {
        return super.createEntityCriteria()
                .createAlias("babSettingHistorys", "setting")
                .add(Restrictions.eq("setting.tagName.name", tagName))
                .add(Restrictions.isNull("setting.lastUpdateTime"))
                .list();
    }

    public List<String> findAllModelName() {
        return super.createEntityCriteria()
                .setProjection(Projections.distinct(Projections.property("modelName")))
                .list();
    }

    public List<Bab> findUnReplyed(int floor_id, DateTime sD, DateTime eD) {
        return super.createEntityCriteria()
                .createAlias("line", "l")
                .createAlias("l.lineType", "lt")
                .add(Restrictions.eq("replyStatus", ReplyStatus.UNREPLIED))
                .add(Restrictions.eq("l.floor.id", floor_id))
                .add(Restrictions.between("beginTime", sD.toDate(), eD.toDate()))
                .list();
    }

    /*
        Select bab with maxium balance record or mininum alarmPercent record
        from BabAlarmHistory table
        Find bab setting in babSettingHistory also.
     */
    public Bab findWithBestBalanceAndSetting(String po) {
        Query q = super.getSession().createQuery(
                "select b from Bab b join b.babSettingHistorys bsh"
                + " join b.babAlarmHistorys bah"
                + " where b.id = ("
                + " select bab.id from babSettingHistorys bs join bs.bab bab"
                + " where bab.po = :po"
                + " and balance = ("
                + " select max(balance) from babSettingHistorys bs2 where bs.bab.id = bs2.bab.id"
                + ")"
                + " )");
        q.setMaxResults(1);
        return (Bab) q.uniqueResult();
    }

    public List<Bab> findByModelNames(List<String> modelNames) {
        return super.createEntityCriteria()
                .add(Restrictions.in("modelName", modelNames))
                .list();
    }

    public List<Bab> findByModelNamesAndLines(List<String> modelNames, List<Line> lines) {
        return super.createEntityCriteria()
                .add(Restrictions.in("modelName", modelNames))
                .add(Restrictions.in("line", lines))
                .list();
    }
    
    public List<Bab> findByPreAssyModuleType(int preAssyModuleTypeId, String po) {
        return super.createEntityCriteria()
                .createAlias("preAssyModuleTypes", "type")
                .setFetchMode("line", FetchMode.JOIN)
                .setFetchMode("babSettingHistorys", FetchMode.JOIN)
                .setFetchMode("babSettingHistorys.babPreAssyPcsRecords", FetchMode.JOIN)
                .add(Restrictions.eq("po", po))
                .add(Restrictions.eq("ispre", 1))
                .add(Restrictions.eq("type.id", preAssyModuleTypeId))
                .addOrder(Order.desc("id"))
                .setMaxResults(5)
                .list();
    }

    @Override
    public int insert(Bab pojo) {
        this.getSession().save(pojo);
        return 1;
    }

    @Override
    public int update(Bab pojo) {
        this.getSession().update(pojo);
        return 1;
    }

    @Override
    public int delete(Bab pojo) {
        this.getSession().delete(pojo);
        return 1;
    }
}
