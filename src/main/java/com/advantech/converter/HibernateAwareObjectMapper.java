/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import javax.annotation.PostConstruct;

/**
 *
 * @author Wei.Cheng
 */
public class HibernateAwareObjectMapper extends ObjectMapper {

    public HibernateAwareObjectMapper() {
        Hibernate5Module hbm = new Hibernate5Module();
        hbm.enable(Hibernate5Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
        hbm.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        registerModule(hbm);
        registerModule(new JodaModule());
    }

    @PostConstruct
    public void afterPropertiesSet() {
//        configure(SerializationFeature.EAGER_SERIALIZER_FETCH, true);
    }

}
