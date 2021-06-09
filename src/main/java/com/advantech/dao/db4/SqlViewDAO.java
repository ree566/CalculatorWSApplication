/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db4;

import com.advantech.model.view.db4.MesChangeTimeInfo;
import com.advantech.model.view.db4.MesPassStationInfo;
import com.advantech.model.view.db4.MesPoDetail;
import com.advantech.webservice.mes.Section;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository(value = "sqlViewDAO4")
public class SqlViewDAO extends AbstractDao<Integer, Object> {

    private DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

    public List findMesPassStationInfo(List<String> po, DateTime dt) {

        String qry = "SELECT c.WIP_NO,C.PLAN_QTY,E.ITEM_NO "
                + ",SUM(case when a.C_DATE = to_date('"
                + fmt.print(dt)
                + "','yyyy-mm-dd') then A.PASS_QTY else 0 end) AS PASS_QTY, "
                + "SUM(case when a.C_DATE <= to_date('"
                + fmt.print(dt)
                + "','yyyy-mm-dd') then A.TOTAL_PASS_CNT else 0 end) AS TOTAL_PASS_QTY "
                + ",LTRIM(C.UNIT_NO) UNIT_NO, a.STATION_ID STATION_ID "
                + "FROM MES.WIP_EFFICIENCY_DETAIL a, MES.WIP_INFO c, MES.WIP_ATT e, MES.LINE_INFO d "
                + "WHERE a.WIP_ID = c.WIP_ID  "
                + "AND c.WIP_NO = e.WIP_NO  "
                + "AND C.LINE_ID = d.LINE_ID  "
                + "AND c.WIP_NO in (?) "
                + "GROUP BY a.RULE_STATION_ID, e.ITEM_NO, c.WIP_NO, C.PLAN_QTY, C.UNIT_NO, a.STATION_ID "
                + "HAVING SUM(A.TOTAL_PASS_CNT) > 0 "
                + "order by c.WIP_NO, C.UNIT_NO, a.STATION_ID ";

        SQLQuery query = this.queryIn(qry, po);

        return query.setResultTransformer(Transformers.aliasToBean(MesPassStationInfo.class))
                .list();
    }

    public List findMesChangeTimeDetail(List<String> po) {

        String qry = "select WIP_NO, UNIT_NO, ITEM_NO, CHANGE_TIME, STANDARD_CHANGE_TIME, POWER_CNT, LINE_DESC "
                + "from MES.V_CHANGE_TIME_DETAIL WHERE "
                + "WIP_NO in(?) "
                + "order by WIP_NO, UNIT_NO ";

        SQLQuery query = this.queryIn(qry, po);

        return query.setResultTransformer(Transformers.aliasToBean(MesChangeTimeInfo.class))
                .list();
    }

    public List findMesPoDetail(List<String> po, Section section) {

        String qry = "SELECT a.WIP_NO,c.LINE_DESC "
                + "FROM MES.WIP_INFO a, "
                + "MES.WIP_STATUS b, MES.LINE_INFO c, MES.FLOW_RULE e, MES.PRODUCT_TYPE g, "
                + "MES.WIP_ATT h "
                + "WHERE a.STATUS_NO = b.STATUS_NO AND a.LINE_ID = c.LINE_ID "
                + "AND a.FLOW_RULE_ID = e.FLOW_RULE_ID AND a.TYPE_ID = g.TYPE_ID "
                + "AND NVL(a.UNIT_NO,NULL) = '" + section.getCode() + "' "
                + "AND a.WIP_NO = h.WIP_NO "
                + "AND a.WIP_NO in (?)";

        SQLQuery query = this.queryIn(qry, po);

        return query.setResultTransformer(Transformers.aliasToBean(MesPoDetail.class))
                .list();
    }

    private SQLQuery queryIn(String qryStr, List<String> params) {
        Map<String, String> replaceStrings = new HashMap();
        for (int i = 0; i < params.size(); i++) {
            replaceStrings.put((":p1" + i), params.get(i));
        }

        qryStr = qryStr.replace("?", String.join(",", replaceStrings.keySet().stream().sorted().collect(Collectors.toList())));
        
        SQLQuery query = super.getSession()
                .createSQLQuery(qryStr);

        replaceStrings.forEach((k, v) -> {
            query.setParameter(k.replace(":", ""), v);
        });

        return query;
    }

}
