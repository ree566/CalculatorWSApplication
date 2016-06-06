/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model;

import com.advantech.entity.BAB;
import com.advantech.entity.LineBalancing;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Wei.Cheng
 */
public class LineBalancingDAO extends BasicDAO {

    private Connection getConn() {
        return getDBUtilConn(SQL.Way_Chien_LineBalcing);
    }

    private List<LineBalancing> getLineBalanceTableWithQuery(String sql, Object... params) {
        return queryForBeanList(getConn(), LineBalancing.class, sql, params);
    }

    public List<LineBalancing> getLineBalance() {
        return getLineBalanceTableWithQuery("SELECT * FROM Line_Balancing_Main_F");
    }

    public List<LineBalancing> getLineBalance(String lineType) {
        return getLineBalanceTableWithQuery("SELECT * FROM Line_Balancing_Main_F WHERE Do_not_stop = ? ORDER BY ID DESC", lineType);
    }

    public LineBalancing getMaxBalance(BAB bab) {
        List lineBalnGroup = getLineBalanceTableWithQuery("SELECT * FROM LS_getBestLineBalnData(?,?,?)",
                bab.getModel_name(),
                bab.getPeople(),
                bab.getLinetype()
        );
        return lineBalnGroup.isEmpty() ? null : (LineBalancing) lineBalnGroup.get(0);
    }
}
