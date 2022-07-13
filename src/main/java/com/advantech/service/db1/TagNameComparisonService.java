/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.TagNameComparison;
import com.advantech.dao.db1.TagNameComparisonDAO;
import com.advantech.model.db1.Floor;
import com.advantech.model.db1.SensorTransform;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class TagNameComparisonService {

    @Autowired
    private TagNameComparisonDAO tagNameComparisonDAO;

    public List<TagNameComparison> findAll() {
        return tagNameComparisonDAO.findAll();
    }

    public TagNameComparison findByPrimaryKey(Object obj_id) {
        return tagNameComparisonDAO.findByPrimaryKey(obj_id);
    }

    public TagNameComparison findByEncode(String encodeStr) {
        return tagNameComparisonDAO.findByEncode(encodeStr);
    }

    public TagNameComparison findByLampSysTagName(String tagName) {
        return tagNameComparisonDAO.findByLampSysTagName(tagName);
    }

    public List<TagNameComparison> findByLine(Integer... line_id) {
        return tagNameComparisonDAO.findByLine(line_id);
    }

    public TagNameComparison findByLineAndStation(int line_id, int station) {
        return tagNameComparisonDAO.findByLineAndStation(line_id, station);
    }

    public List<TagNameComparison> findInRange(SensorTransform startPosition, int maxiumStation) {
        return tagNameComparisonDAO.findInRange(startPosition, maxiumStation);
    }

    public List<TagNameComparison> findInRange(TagNameComparison startPosition, int maxiumStation) {
        return tagNameComparisonDAO.findInRange(startPosition, maxiumStation);
    }

    public List<TagNameComparison> findByFloorName(String floorName) {
        return tagNameComparisonDAO.findByFloorName(floorName);
    }

    public int insert(TagNameComparison pojo) {
        return tagNameComparisonDAO.insert(pojo);
    }

    public int update(TagNameComparison pojo) {
        return tagNameComparisonDAO.update(pojo);
    }

    public int delete(TagNameComparison pojo) {
        return tagNameComparisonDAO.delete(pojo);
    }

}
