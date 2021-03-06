/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.model.db1.Line;
import com.advantech.dao.db1.LineDAO;
import com.advantech.model.db1.LineType;
import com.advantech.model.db1.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class LineService {

    @Autowired
    private LineDAO lineDAO;

    public List<Line> findAll() {
        return lineDAO.findAll();
    }

    public Line findByPrimaryKey(Object obj_id) {
        return lineDAO.findByPrimaryKey(obj_id);
    }

    public List<Line> findBySitefloor(int floor_id) {
        return lineDAO.findBySitefloor(floor_id);
    }

    public List<Line> findBySitefloor(String floor_name) {
        return lineDAO.findBySitefloor(floor_name);
    }

    public List<Line> findBySitefloorAndLineType(String floorName, List<LineType> lineType) {
        return lineDAO.findBySitefloorAndLineType(floorName, lineType);
    }

    public List<Line> findByLineType(List<LineType> lineType) {
        return lineDAO.findByLineType(lineType);
    }

    public List<Line> findWithLineType() {
        return lineDAO.findWithLineType();
    }

    public LineType findLineType(int line_id) {
        return lineDAO.findLineType(line_id);
    }

    public List<Line> findByUser(User user) {
        return lineDAO.findByUser(user);
    }

    public List<Line> findByUserAndLineType(User user, List<LineType> lt) {
        return lineDAO.findByUserAndLineType(user, lt);
    }

    public int insert(Line pojo) {
        return lineDAO.insert(pojo);
    }

    public int update(Line pojo) {
        return lineDAO.update(pojo);
    }

    public int delete(Line pojo) {
        return lineDAO.delete(pojo);
    }

}
