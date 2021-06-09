/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db6;

import com.advantech.model.view.db6.ShortageInfo;
import java.util.List;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository(value = "sqlViewDAO6")
public class SqlViewDAO extends AbstractDao<Integer, Object> {

    public List<ShortageInfo> findShortageInfo() {

        String qry = "select TOP (100) PERCENT  "
                + "b.訂單 po, "
                + "b.物料 material, "
                + "cast(b.缺料數量 as int) shortageCnt "
                + "from 備料明細 b JOIN prepare_Schedule s on b.訂單 = s.PO  "
                + "JOIN prepare_state st on s.prepare_stateID = st.prepare_stateID  "
                + "where 缺料數量 >'0' and st.prepare_stateID <> '-1'  "
                + "ORDER BY b.訂單, b.物料 ";

        SQLQuery query = super.getSession()
                .createSQLQuery(qry);

        return query.setResultTransformer(Transformers.aliasToBean(ShortageInfo.class))
                .list();
    }

}
