/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.model;

import com.advantech.entity.AlarmAction;
import com.advantech.entity.BAB;
import com.advantech.entity.BABHistory;
import com.advantech.entity.LineBalancing;
import com.advantech.helper.ProcRunner;
import com.advantech.helper.PropertiesReader;
import com.advantech.interfaces.AlarmActions;
import com.advantech.service.BasicService;
import com.advantech.service.LineBalanceService;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Wei.Cheng bab資料表就是生產工單資料表
 */
public class BABDAO extends BasicDAO implements AlarmActions {

    private static final Logger log = LoggerFactory.getLogger(BABDAO.class);

    private static boolean saveToOldDB;

    public BABDAO() {
        PropertiesReader p = PropertiesReader.getInstance();
        saveToOldDB = p.isSaveToOldDB();
    }

    private Connection getConn() {
        return getDBUtilConn(SQL.WebAccess);
    }

    private List<BAB> queryBABTable(String sql, Object... params) {
        return queryForBeanList(getConn(), BAB.class, sql, params);
    }

    private List<BABHistory> getHistoryTable(String sql, Object... params) {
        return queryForBeanList(getConn(), BABHistory.class, sql, params);
    }

    public List<BAB> getBAB() {
        return queryBABTable("SELECT * FROM LS_BAB");
    }

    public BAB getBAB(int BABid) {
        List l = queryBABTable("SELECT * FROM LS_BAB WHERE id = ?", BABid);
        return !l.isEmpty() ? (BAB) l.get(0) : null;
    }

    public List<Map> getBABForMap() {
        return queryForMapList(getConn(), "SELECT * FROM closedBABView");
    }

    public List<Map> getBABForMap(int BABid) {
        return queryForMapList(getConn(), "SELECT * FROM closedBABView WHERE id = ?", BABid);
    }

    public List<Map> getBABForMap(String date) {
        return queryForMapList(getConn(), "SELECT * FROM closedBABView WHERE CONVERT(varchar(10),btime,20) = ? ORDER BY ID", date);
    }

    public List<BAB> getBAB(String modelName, String dateFrom, String dateTo) {
        return queryProcForBeanList(getConn(), BAB.class, "{CALL getBABInTime(?,?,?)}", modelName, dateFrom, dateTo);
    }

    //Quartz用，獲得需要計算ouput的工單
    public List<BAB> getAllProcessing() {
        return queryBABTable("SELECT * FROM LS_BAB_Id_List");
    }

    public List<BAB> getAssyProcessing() {
        return queryBABTable("SELECT * FROM assyProcessing");
    }

    public List<BABHistory> getBABHistory(BAB bab) {
        return getHistoryTable("SELECT * FROM LS_BAB_History WHERE BABid = ?", bab.getId());
    }

    public List<Map> getBABInfo(String startDate, String endDate) {
        return queryProcForMapList(getConn(), "{CALL getBABDetail_1(?,?)}", startDate, endDate);
    }

    public List<BAB> getProcessingBAB() {
        return queryBABTable("SELECT * FROM LS_BAB_Sort");
    }

    public BAB getProcessingBAB(int BABid) {
        List l = queryBABTable("SELECT * FROM LS_BAB_Sort WHERE id = ?", BABid);
        return !l.isEmpty() ? (BAB) l.get(0) : null;
    }

    public List<BAB> getProcessingBABByLine(int lineNo) {
        return queryBABTable("SELECT * FROM LS_BAB_Sort WHERE line = ? ORDER BY id", lineNo);
    }

    public List<BAB> getProcessingBABByPOAndLine(String PO, int line) {
        return queryBABTable("SELECT * FROM LS_BAB_Sort WHERE PO = ? AND line = ?", PO, line);
    }

    public List<BAB> getTimeOutBAB() {
        return queryBABTable("SELECT * FROM LS_BABTimeOutView");//get the timeout bab where hour diff = 2
    }

    public BAB getLastInputBAB(int lineNo) {
        List list = queryBABTable("SELECT TOP 1 * FROM LS_BAB_Sort WHERE line = ? ORDER BY ID DESC", lineNo);
        return list.isEmpty() ? null : (BAB) list.get(0);
    }

    public List<Map> getLastGroupStatus(int BABid) {
        return queryProcForMapList(getConn(), "{CALL LS_lastGroupStatus(?)}", BABid);
    }

    public List<Map> getBABAvgs(int BABid) {
        return queryForMapList(getConn(), "SELECT * FROM BABAVG(?)", BABid);
    }

    public List<Map> getBABAvgsInSpecGroup(int BABid, int groupStart, int groupEnd) {
        return queryProcForMapList(getConn(), "{CALL getbabAvgInSpecGroup(?,?,?)}", BABid, groupStart, groupEnd);
    }

    public List<Map> getClosedBABAVG(int BABid) throws JSONException {
        return queryForMapList(getConn(), "SELECT * FROM closedBABAVG(?)", BABid);
    }

    public List<Map> getSensorStatus(int BABid) {
        return queryForMapList(getConn(), "SELECT * FROM getSensorStatusByBabId(?) order by 1,2", BABid);
    }

    public List<Map> getBABTimeHistoryDetail(int BABid) {
        return queryForMapList(getConn(), "SELECT * FROM LS_BABTimeHistoryDetail WHERE BABid = ? ORDER BY TagName, groupid", BABid);
    }

    public List<Map> getBalancePerGroup(int BABid) {
        return queryForMapList(getConn(), "SELECT * FROM LS_balanceDetailPerGroup(?)", BABid);
    }

    public List<Map> getClosedBalanceDetail(int BABid) {
        return queryForMapList(getConn(), "SELECT * FROM LS_BalanceHistory WHERE BABid = ?", BABid);
    }

    public List<Map> getLineBalanceCompare(String Model_name, String lineType) {
        return queryForMapList(getConn(), "SELECT * FROM LS_LineBalanceCompare(?,?)", Model_name, lineType);
    }

    public List<Map> getLineBalanceCompare(int BAbid) {
        return queryForMapList(getConn(), "SELECT * FROM LS_LineBalanceCompareById(?)", BAbid);
    }

    public List<Array> getAvailableModelName() {
        return queryForArrayList(getConn(), "SELECT Model_name from LS_availModelName");
    }

    public boolean checkSensorIsClosed(int BABid, int sensorNo) {
        List historys = getHistoryTable("SELECT * FROM LS_BAB_History WHERE BABid = ? and T_Num = ?", BABid, sensorNo);
        return !historys.isEmpty();//回傳是否有東西 有true 無 false
    }

    public Integer getPoTotalQuantity(String PO) {
        List<Map> l = queryForMapList(this.getConn(), "SELECT * FROM poQuantityView WHERE PO = ?", PO);
        return l.isEmpty() ? null : (Integer) l.get(0).get("qty");
    }

    @Override
    public boolean insertAlarm(List<AlarmAction> l) {
        return updateAlarmTable("INSERT INTO Alm_BABAction(alarm, tableId) VALUES(?, ?)", l);
    }

    @Override
    public boolean updateAlarm(List<AlarmAction> l) {
        return updateAlarmTable("UPDATE Alm_BABAction SET alarm = ? WHERE tableId = ?", l);
    }

    @Override
    public boolean resetAlarm() {
        return update(getConn(), "UPDATE Alm_BABAction SET alarm = 0");
    }

    @Override
    public boolean removeAlarmSign() {
        return update(getConn(), "TRUNCATE TABLE Alm_BABAction");
    }

    private boolean updateAlarmTable(String sql, List<AlarmAction> l) {
        return update(getConn(), sql, l, "alarm", "tableId");
    }

    public boolean setBABAlarmToTestingMode() {
        return update(getConn(), "UPDATE Alm_BABAction SET alarm = 1");
    }

    public boolean insertBAB(BAB bab) {
        return update(
                getConn(),
                "INSERT INTO LS_BAB(PO,Model_name,line,people,startPosition) VALUES (?,?,?,?,?)",
                bab.getPO(),
                bab.getModel_name(),
                bab.getLine(),
                bab.getPeople(),
                bab.getStartPosition()
        );
    }

    /**
     * Please set the babAvg into bab object if data need to saveAndClose.
     *
     * @param bab
     * @return
     */
    //一連串儲存動作統一commit，不然出問題時會出現A和B資料庫資料不同步問題
    public boolean stopAndSaveBab(BAB bab) {
        LineBalanceService lineBalanceService = BasicService.getLineBalanceService();

        boolean flag = false;
        Connection conn1 = null;

        try {
            //Prevent check Babavg data in database if exists or not multiple times, let ouside check and save value into bab object.
            JSONArray balances = bab.getBabavgs();
            if (balances == null) {// check data balance is exist first
                log.error("The babAvg in bab object is not setting value, saving action suspend.");
                return false;
            }

            double baln = lineBalanceService.caculateLineBalance(balances);

            QueryRunner qRunner = new QueryRunner();
            ProcRunner pRunner = new ProcRunner();

            //--------區間內請勿再開啟tran不然會deadlock----------------------------
            conn1 = this.getConn();
            conn1.setAutoCommit(false);

            Object[] param3 = {bab.getId()};
            pRunner.updateProc(conn1, "{CALL LS_closeBABWithSaving(?)}", param3);//關閉線別

            //--------區間內請勿再開啟tran不然會deadlock----------------------------
            DbUtils.commitAndCloseQuietly(conn1);
            flag = true;
        } catch (SQLException ex) {
            log.error(ex.toString());
            DbUtils.rollbackAndCloseQuietly(conn1);
        } 
        return flag;
    }

    public boolean stopSingleSensor(int sensorId, int BABid) {
        return updateProc(getConn(), "{CALL LS_Sensor_END(?,?)}", sensorId, BABid);
    }

    public boolean closeBABDirectly(BAB bab) {
        return updateProc(getConn(), "{call LS_closeBABDirectly(?)}", bab.getId());
    }
}
