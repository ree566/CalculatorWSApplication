/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db5;

import com.advantech.model.view.db5.LackingInfo;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository(value = "sqlViewDAO5")
public class SqlViewDAO extends AbstractDao<Integer, Object> {

    public List<LackingInfo> findLackingInfo() {

        String qry = "SELECT "
                + "items.label_1 AS po, "
                + "items.label_3 AS material, "
                + "orders.number AS qty, "
                + "orders.comment "
                + "FROM "
                + "orders, items, users "
                + "WHERE "
                + "orders.id = items.order_id && "
                + "orders.user_id = users.id && "
                + "orders.time_close = 0 "
                + "ORDER BY items.label_1, items.label_3 ";

        SQLQuery query = super.getSession()
                .createSQLQuery(qry);

        return query.setResultTransformer(Transformers.aliasToBean(LackingInfo.class))
                .list();
    }

}
