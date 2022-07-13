/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.quartzJob;

import com.advantech.model.db1.Worktime;
import com.advantech.service.db1.WorktimeService;
import com.advantech.service.db3.SqlViewService;
import java.util.List;
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
//@Transactional
public class SyncWorktimeFromRemote {

    private static final Logger logger = LoggerFactory.getLogger(SyncWorktimeFromRemote.class);

    @Autowired
    private WorktimeService worktimeService;

    @Autowired
    @Qualifier("sqlViewService3")
    private SqlViewService sqlViewService;

    public void execute() throws Exception {
        List<Worktime> remoteData = sqlViewService.findWorktime();

        worktimeService.deleteAll();
        worktimeService.insert(remoteData);
        
    }

}
