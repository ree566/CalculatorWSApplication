/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db7;

import com.advantech.dao.db7.SqlViewDAO;
import com.advantech.model.db1.Worktime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service(value = "sqlViewService7")
@Transactional("transactionManager7")
public class SqlViewService {

    @Autowired
    @Qualifier("sqlViewDAO7")
    private SqlViewDAO sqlViewDAO;

    public List<Worktime> findWorktime() {
        return sqlViewDAO.findWorktime();
    }

}
