/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model;

import com.advantech.entity.Cell;
import com.advantech.entity.TagNameComparison;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Wei.Cheng
 */
public class TagNameComparisonDAO extends BasicDAO {

    public TagNameComparisonDAO() {

    }

    private Connection getConn() {
        return getDBUtilConn(SQL.Way_Chien_WebAccess);
    }

    private List<TagNameComparison> query(String sql, Object... params) {
        return queryForBeanList(getConn(), TagNameComparison.class, sql, params);
    }

    public List<TagNameComparison> getAll() {
        return query("SELECT * FROM LS_TagNameComparison");
    }

    public List<TagNameComparison> getOne(String orginTagName) {
        return query("SELECT * FROM LS_TagNameComparison WHERE orginTagName = ?", orginTagName);
    }

    public boolean insert(List<TagNameComparison> l) {
        return update(
                getConn(),
                "INSERT INTO LS_TagNameComparison(orginTagName, lampSysTagName, lineId, stationId) VALUES(?,?,?,?)",
                l,
                "orginTagName", "lampSysTagName", "lineId", "stationId");
    }

    public boolean update(List<TagNameComparison> l) {
        return update(
                getConn(),
                "UPDATE LS_TagNameComparison SET lineId = ?, stationId = ? WHERE orginTagName = ?",
                l,
                "lineId", "stationId", "orginTagName");
    }

    public boolean delete(List<TagNameComparison> l) {
        return update(getConn(), "DELETE LS_TagNameComparison WHERE orginTagName = ?", l, "orginTagName");
    }

    public boolean deleteOne(TagNameComparison tagNameComparison) {
        return update(getConn(), "DELETE LS_TagNameComparison WHERE orginTagName = ?", tagNameComparison.getOrginTagName());
    }

}