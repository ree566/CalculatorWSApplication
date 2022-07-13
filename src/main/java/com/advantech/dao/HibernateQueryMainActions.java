/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

/**
 *
 * @author Wei.Cheng
 * @param <PK>
 * @param <T>
 */
public abstract class HibernateQueryMainActions<PK extends Serializable, T> {

    private final Class<T> persistentClass;

    private SessionFactory sessionFactory;

    @PostConstruct
    public abstract void setSessionFactory();

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public HibernateQueryMainActions() {
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

    protected Query queryIn(String qryStr, List<String> params) {
        Map<String, String> replaceStrings = new HashMap();
        for (int i = 0; i < params.size(); i++) {
            replaceStrings.put((":p1" + i), params.get(i));
        }

        qryStr = qryStr.replace("?", String.join(",", replaceStrings.keySet().stream().sorted().collect(Collectors.toList())));
        
        Query query = this.getSession()
                .createSQLQuery(qryStr);

        replaceStrings.forEach((k, v) -> {
            query.setParameter(k.replace(":", ""), v);
        });

        return query;
    }
}
