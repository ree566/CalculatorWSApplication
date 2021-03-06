/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.FqcModelStandardTimeDAO;
import com.advantech.model.db1.FqcModelStandardTime;
import static com.google.common.base.Preconditions.checkArgument;
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
public class FqcModelStandardTimeService {

    @Autowired
    private FqcModelStandardTimeDAO fqcModelStandardTimeDAO;

    public List<FqcModelStandardTime> findAll() {
        return fqcModelStandardTimeDAO.findAll();
    }

    public FqcModelStandardTime findByPrimaryKey(Object obj_id) {
        return fqcModelStandardTimeDAO.findByPrimaryKey(obj_id);
    }

    public List<FqcModelStandardTime> findByName(String modelSeries) {
        return fqcModelStandardTimeDAO.findByName(modelSeries);
    }

    public int insert(FqcModelStandardTime pojo) {
        return fqcModelStandardTimeDAO.insert(pojo);
    }

    public int update(FqcModelStandardTime pojo) {
        return fqcModelStandardTimeDAO.update(pojo);
    }

    public int delete(FqcModelStandardTime pojo) {
        return fqcModelStandardTimeDAO.delete(pojo);
    }

}
