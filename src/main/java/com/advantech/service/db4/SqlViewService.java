/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db4;

import com.advantech.dao.db4.SqlViewDAO;
import com.advantech.model.view.db4.MesChangeTimeInfo;
import com.advantech.model.view.db4.MesPassStationInfo;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service(value = "sqlViewService4")
@Transactional("transactionManager4")
public class SqlViewService {

    @Autowired
    @Qualifier("sqlViewDAO4")
    private SqlViewDAO sqlViewDAO;

    public List<MesPassStationInfo> findMesPassStationInfo(List<String> po, DateTime dt) {
        return sqlViewDAO.findMesPassStationInfo(po, dt);
    }

    public List<MesChangeTimeInfo> findMesChangeTimeDetail(List<String> po) {
        return sqlViewDAO.findMesChangeTimeDetail(po);
    }

}
