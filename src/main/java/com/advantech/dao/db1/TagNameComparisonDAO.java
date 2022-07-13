/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.dao.db1;

import com.advantech.model.db1.SensorTransform;
import com.advantech.model.db1.TagNameComparison;
import com.advantech.model.db1.TagNameComparisonId;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Wei.Cheng
 */
@Repository
public class TagNameComparisonDAO extends AbstractDao<TagNameComparisonId, TagNameComparison> implements BasicDAO_1<TagNameComparison> {

    @Override
    public List<TagNameComparison> findAll() {
        return super.createEntityCriteria().list();
    }

    @Override
    public TagNameComparison findByPrimaryKey(Object obj_id) {
        return super.getByKey((TagNameComparisonId) obj_id);
    }

    public TagNameComparison findByEncode(String encodeStr) {
        Criteria c = super.createEntityCriteria();
        c.createAlias("line", "line");
        c.createAlias("line.floor", "f");
        c.add(Restrictions.eq("tagNameEncode", encodeStr));
        return (TagNameComparison) c.uniqueResult();
    }

    public TagNameComparison findByLampSysTagName(String tagName) {
        return (TagNameComparison) super.createEntityCriteria()
                .createAlias("line", "line")
                .createAlias("line.lineType", "lt")
                .add(Restrictions.eq("id.lampSysTagName.name", tagName))
                .uniqueResult();
    }

    public TagNameComparison findByLampSysTagName(SensorTransform tag) {
        return (TagNameComparison) super.createEntityCriteria()
                .add(Restrictions.eq("id.lampSysTagName", tag))
                .uniqueResult();
    }

    public List<TagNameComparison> findByLine(Integer... line_id) {
        return super.createEntityCriteria()
                .createAlias("line", "l")
                .add(Restrictions.in("l.id", line_id))
                .list();
    }

    public TagNameComparison findByLineAndStation(int line_id, int station) {
        return (TagNameComparison) super.createEntityCriteria()
                .add(Restrictions.eq("line.id", line_id))
                .add(Restrictions.eq("position", station))
                .uniqueResult();
    }

    public List<TagNameComparison> findInRange(SensorTransform startPosition, int maxiumStation) {
        TagNameComparison tag = this.findByLampSysTagName(startPosition);
        return super.createEntityCriteria()
                .add(Restrictions.eq("line.id", tag.getLine().getId()))
                .add(Restrictions.between("position", tag.getPosition(), tag.getPosition() + maxiumStation - 1))
                .addOrder(Order.asc("position"))
                .list();
    }

    public List<TagNameComparison> findInRange(TagNameComparison startPosition, int maxiumStation) {
        return super.createEntityCriteria()
                .add(Restrictions.eq("line.id", startPosition.getLine().getId()))
                .add(Restrictions.between("position", startPosition.getPosition(), startPosition.getPosition() + maxiumStation - 1))
                .addOrder(Order.asc("position"))
                .list();
    }

    public List<TagNameComparison> findByFloorName(String floorName) {
        Criteria c = super.createEntityCriteria();
        c.createAlias("line", "line");
        c.createAlias("line.floor", "f");
        c.add(Restrictions.eq("f.name", floorName));
        return c.list();
    }

    @Override
    public int insert(TagNameComparison pojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(TagNameComparison pojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int delete(TagNameComparison pojo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
