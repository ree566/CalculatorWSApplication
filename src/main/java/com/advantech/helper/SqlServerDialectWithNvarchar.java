/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import java.sql.Types;

/**
 *
 * @author Wei.Cheng
 */
public class SqlServerDialectWithNvarchar extends org.hibernate.dialect.SQLServer2012Dialect {

    public SqlServerDialectWithNvarchar() {
        registerHibernateType(Types.NVARCHAR, 4000, "string");
    }

}
