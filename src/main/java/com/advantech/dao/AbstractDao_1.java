/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Wei.Cheng
 * @param <PK>
 * @param <T>
 */
public abstract class AbstractDao_1<PK extends Serializable, T> {

    private final Class<T> persistentClass;

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    public AbstractDao_1() {
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    protected T getByKey(PK key) {
        return (T) getSession().get(persistentClass, key);
    }

    protected Criteria createEntityCriteria() {
        return getSession().createCriteria(persistentClass);
    }
    
}
