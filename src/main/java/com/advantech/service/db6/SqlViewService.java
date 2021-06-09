/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db6;

import com.advantech.dao.db6.SqlViewDAO;
import com.advantech.model.view.db6.ShortageInfo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service(value = "sqlViewService6")
@Transactional("transactionManager6")
public class SqlViewService {

    @Autowired
    @Qualifier("sqlViewDAO6")
    private SqlViewDAO sqlViewDAO;

    public List<ShortageInfo> findShortageInfo() {
        return sqlViewDAO.findShortageInfo();
    }

}
