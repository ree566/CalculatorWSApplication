/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.listener;

import com.advantech.model.AuditedRevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Wei.Cheng
 */
public class AuditingRevisionListener implements RevisionListener {

    private static final Logger log = LoggerFactory.getLogger(AuditingRevisionListener.class);

    @Override
    public void newRevision(Object revisionEntity) {
        AuditedRevisionEntity revEntity = (AuditedRevisionEntity) revisionEntity;
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        revEntity.setUsername(userName);
    }
}
