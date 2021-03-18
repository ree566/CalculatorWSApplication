/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db3;

import com.advantech.dao.db3.SqlViewDAO;
import com.advantech.model.db1.Worktime;
import com.advantech.model.view.db1.UserInfoRemote;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service(value = "sqlViewService3")
@Transactional("transactionManager3")
public class SqlViewService {

    @Autowired
    @Qualifier("sqlViewDAO3")
    private SqlViewDAO sqlViewDAO;

    public List<Worktime> findWorktime() {
        return sqlViewDAO.findWorktime();
    }
}
