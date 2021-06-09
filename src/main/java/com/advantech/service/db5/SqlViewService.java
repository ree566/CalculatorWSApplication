/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db5;

import com.advantech.dao.db5.SqlViewDAO;
import com.advantech.model.view.db5.LackingInfo;
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
@Service(value = "sqlViewService5")
@Transactional("transactionManager5")
public class SqlViewService {

    @Autowired
    @Qualifier("sqlViewDAO5")
    private SqlViewDAO sqlViewDAO;

    public List<LackingInfo> findLackingInfo() {
        return sqlViewDAO.findLackingInfo();
    }

}
