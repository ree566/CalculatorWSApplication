/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import com.advantech.facade.BabLineTypeFacade;
import com.advantech.facade.FqcLineTypeFacade;
import com.advantech.facade.TestLineTypeFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
    "classpath:servlet-context.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFacade {

    @Autowired
    private BabLineTypeFacade bF;

    @Autowired
    private TestLineTypeFacade tF;

    @Autowired
    private FqcLineTypeFacade fF;

    @Test
    @Transactional
    @Rollback(false)
    public void testBabLineTypeFacade() {
        bF.initMap();
        bF.initAlarmSign();
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testTestLineTypeFacade() {
        tF.initMap();
        tF.initAlarmSign();
    }

//    @Test
    @Transactional
    @Rollback(true)
    public void testFqcLineTypeFacade() {
        fF.generateData();
    }

}
