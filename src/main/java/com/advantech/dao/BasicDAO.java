/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao;

import com.advantech.helper.ProcRunner;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.ReturningWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Wei.Cheng
 */
@Component
public class BasicDAO {

    /*
     change the database driver because of
     http://www.javaworld.com.tw/jute/post/view?bid=6&id=131471&tpg=1&ppg=1&sty=1&age=0
     http://www.javaworld.com.tw/jute/post/view?bid=21&id=366&sty=1&tpg=1&age=-1
        
     old: "jdbc:sqlserver://NB991001\\KEVIN;databaseName=Internal_Check";
     the different is jtds no need to provide the instance name
    
     http://simon-tech.blogspot.tw/2012/02/tomcat-datasource.html
     建議將 JDBC driver 放在 $CATALINA_BASE/lib 路徑下，以免造成 JRE Memory Leak 的問題
     */
    private static final Logger log = LoggerFactory.getLogger(BasicDAO.class);

    private QueryRunner qRunner;

    private ProcRunner pRunner;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    @Qualifier(value = "sessionFactory")
    private SessionFactory sessionFactory2;

    public static enum SQL {
        TWM3,
        WebAccess,
        LineBalancing;
    }

    public Connection getDBUtilConn(final SQL sqlType) {
        Connection conn = null;
        switch (sqlType) {
            case TWM3:
                return null;
            case WebAccess:
                Session session = sessionFactory.getCurrentSession();
                conn = session.doReturningWork(new ReturningWork<Connection>() {
                    @Override
                    public Connection execute(Connection conn) throws SQLException {
                        return conn;
                    }
                });
                break;
            case LineBalancing:
                conn = sessionFactory2.getCurrentSession().doReturningWork(new ReturningWork<Connection>() {
                    @Override
                    public Connection execute(Connection conn) throws SQLException {
                        return conn;
                    }
                });
                break;
        }
        return conn;
    }

    public List<Map> queryForMapList(Connection conn, String sql, Object... params) {
        return query(conn, new MapListHandler(), sql, params);
    }

    public List queryForBeanList(Connection conn, Class cls, String sql, Object... params) {
        return query(conn, new BeanListHandler(cls), sql, params);
    }

    public List<Array> queryForArrayList(Connection conn, String sql, Object... params) {
        return query(conn, new ArrayListHandler(), sql, params);
    }

    private List query(Connection conn, ResultSetHandler rsh, String sql, Object... params) {
        List<?> data = null;
        qRunner = new QueryRunner();
        try {
            data = (List) qRunner.query(conn, sql, rsh, params);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return data == null ? new ArrayList() : data;
    }

    public boolean update(Connection conn, String sql, List l, String... params) {
        boolean flag = false;
        qRunner = new QueryRunner();
        try {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Object o : l) {
                    qRunner.fillStatementWithBean(ps, o, params);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            flag = true;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return flag;
    }

    public boolean update(Connection conn, String sql, Object... params) {
        boolean flag = false;
        qRunner = new QueryRunner();
        try {
            qRunner.update(conn, sql, params);
            flag = true;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return flag;
    }

    public boolean updateProc(Connection conn, String sql, Object... params) {
        boolean flag = false;
        pRunner = new ProcRunner();
        try {
            pRunner.updateProc(conn, sql, params);
            flag = true;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return flag;
    }

    public List queryProcForBeanList(Connection conn, Class cls, String sql, Object... params) {
        return queryProc(conn, new BeanListHandler(cls), sql, params);
    }

    public List<Map> queryProcForMapList(Connection conn, String sql, Object... params) {
        return queryProc(conn, new MapListHandler(), sql, params);
    }

    private List queryProc(Connection conn, ResultSetHandler rsh, String sql, Object... params) {
        List data = null;
        pRunner = new ProcRunner();
        try {
            data = (List) pRunner.queryProc(conn, sql, rsh, params);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return data == null ? new ArrayList() : data;
    }

}
