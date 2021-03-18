/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.Bab;
import com.advantech.model.view.db1.BabAvg;
import java.util.List;
import java.util.Map;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class SqlViewDAO extends AbstractDao<Integer, Object> {

    public List<BabAvg> findBabAvg(int bab_id) {
        return super.getSession()
                .createSQLQuery("select * from {h-schema}tbfn_BabAvg(:bab_id)")
                .addScalar("bab_id", StandardBasicTypes.INTEGER)
                .addScalar("station", StandardBasicTypes.INTEGER)
                .addScalar("average", StandardBasicTypes.DOUBLE)
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.aliasToBean(BabAvg.class))
                .list();
    }

    public List<BabAvg> findBabAvgWithBarcode(int bab_id) {
        return super.getSession()
                .createSQLQuery("select * from {h-schema}tbfn_BabAvg_WithBarcode(:bab_id)")
                .addScalar("bab_id", StandardBasicTypes.INTEGER)
                .addScalar("station", StandardBasicTypes.INTEGER)
                .addScalar("average", StandardBasicTypes.DOUBLE)
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.aliasToBean(BabAvg.class))
                .list();
    }

    public List<BabAvg> findBabAvgInHistory(int bab_id) {
        return super.getSession()
                .createSQLQuery("select * from {h-schema}tbfn_BabAvg_history(:bab_id)")
                .addScalar("bab_id", StandardBasicTypes.INTEGER)
                .addScalar("station", StandardBasicTypes.INTEGER)
                .addScalar("average", StandardBasicTypes.DOUBLE)
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.aliasToBean(BabAvg.class))
                .list();
    }

    public List<Map> findSensorStatus(int bab_id) {
        return super.getSession()
                .createSQLQuery("select * from {h-schema}tbfn_GetSensorStatus(:bab_id) ORDER BY groupid, station")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBarcodeStatus(int bab_id) {
        return super.getSession()
                .createSQLQuery("select * from {h-schema}tbfn_GetBarcodeStatus(:bab_id) ORDER BY groupid, station")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBalanceDetail(int bab_id) {
        return super.getSession()
                .createSQLQuery("select * from {h-schema}tbfn_BabBalanceDetail(:bab_id)")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findBalanceDetailWithBarcode(int bab_id) {
        return super.getSession()
                .createSQLQuery("select * from {h-schema}tbfn_BabBalanceDetailWithBarcode(:bab_id)")
                .setParameter("bab_id", bab_id)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Map> findSensorStatusPerStationToday() {
        return super.getSession()
                .createSQLQuery("select * from {h-schema}vw_SensorStatusPerStationToday")
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .list();
    }

    public List<Bab> findBabLastInputPerLine() {
        return super.getSession()
                .createSQLQuery("select * from {h-schema}vw_BabLastInputPerLine order by line_id, id")
                .addEntity(Bab.class)
                .list();
    }

}
