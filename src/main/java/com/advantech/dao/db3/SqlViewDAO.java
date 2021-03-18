/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db3;

import com.advantech.model.db1.Worktime;
import java.util.List;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository(value = "sqlViewDAO3")
public class SqlViewDAO extends AbstractDao<Integer, Object> {

    public List<Worktime> findWorktime() {
        return super.getSession()
                .createSQLQuery("select modelName, floorName, speOwnerName, eeOwnerName, qcOwnerName, "
                        + "assy assy, t1 t1, t2 t2, t3 t3, t4 t4, "
                        + "packing packing, totalModule preAssy, assyStation assyPeople, packingStation packingPeople, packingLeadTime "
                        + "from Sheet_Main_view")
                .setResultTransformer(Transformers.aliasToBean(Worktime.class))
                .list();
    }

}
