/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.Test;
import com.advantech.dao.db1.TestDAO;
import com.advantech.model.db1.TestTable;
import com.advantech.webservice.Factory;
import com.advantech.webservice.WebServiceTX;
import static com.google.common.base.Preconditions.*;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestService {

    @Autowired
    private TestDAO testDAO;

    @Autowired
    private TestTableService testTableService;

    @Autowired
    private WebServiceTX tx;

    private static final Logger log = LoggerFactory.getLogger(TestService.class);

    public List<Test> findAll() {
        return testDAO.findAll();
    }

    public Test findByPrimaryKey(Object obj_id) {
        return testDAO.findByPrimaryKey(obj_id);
    }

    public int insert(Test pojo) {
        return testDAO.insert(pojo);
    }

    public int insert(int table_id, String jobnumber) {
        TestTable table = testTableService.findByPrimaryKey(table_id);
        checkDeskIsAvailable(table);
        checkUserIsAvailable(jobnumber);
        Test t = new Test(table, jobnumber);
        t.setLastUpdateTime(new DateTime().toDate());
        this.insert(t);
        try {
            Factory f = Factory.TWM3;
            if (table.getFloor().getId() == 3) {
                f = Factory.TWM6;
            }
            tx.kanbanUserLogin(jobnumber, f);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return 1;
    }

    public void checkDeskIsAvailable(TestTable t) {
        checkArgument(t.getTests() == null || t.getTests().isEmpty(), "此桌次已有使用者");
    }

    public void checkUserIsAvailable(String jobNumber) {
        Test t = testDAO.findByJobnumber(jobNumber);
        checkArgument(t == null, "使用者已在其它桌次使用中");
    }

    public int update(Test pojo) {
        return testDAO.update(pojo);
    }

    public int changeDeck(String jobnumber) {
        Test t = testDAO.findByJobnumber(jobnumber);
        this.delete(t);
        return 1;
    }

    public int delete(Test pojo) {
        return testDAO.delete(pojo);
    }

    public int delete(String jobnumber) {
        Test t = testDAO.findByJobnumber(jobnumber);
        this.delete(t);
        Factory f = Factory.TWM3;
        if (t.getTestTable().getFloor().getId() == 3) {
            f = Factory.TWM6;
        }
        tx.kanbanUserLogout(jobnumber, f);
        return 1;
    }

    public int cleanTests() {
        return testDAO.cleanTests();
    }

}
