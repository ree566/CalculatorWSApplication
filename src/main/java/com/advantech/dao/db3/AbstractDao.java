/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db3;

import com.advantech.dao.HibernateQueryMainActions;
import java.io.Serializable;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Wei.Cheng
 * @param <PK>
 * @param <T>
 */
public abstract class AbstractDao<PK extends Serializable, T> extends HibernateQueryMainActions<PK, T> {

    @Autowired
    @Qualifier("sessionFactory3")
    private SessionFactory sessionFactory;

    @Override
    public void setSessionFactory() {
        super.setSessionFactory(sessionFactory);
    }
}
