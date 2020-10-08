/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.service.db1;

import com.advantech.dao.db1.BabPassStationRecordDAO;
import com.advantech.model.db1.Bab;
import com.advantech.model.db1.BabPassStationRecord;
import com.advantech.model.db1.BabSettingHistory;
import com.advantech.webservice.Factory;
import com.advantech.webservice.WebServiceRV;
import static com.google.common.base.Preconditions.*;
import java.util.List;
import java.util.Objects;
import static java.util.stream.Collectors.toList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wei.Cheng
 */
@Service
@Transactional
public class BabPassStationRecordService {

    private static final Logger log = LoggerFactory.getLogger(BabPassStationRecordService.class);

    @Autowired
    private BabPassStationRecordDAO dao;

    @Autowired
    private WebServiceRV rv;

    @Autowired
    private BabSettingHistoryService babSettingService;

    public List<BabPassStationRecord> findAll() {
        return dao.findAll();
    }

    public BabPassStationRecord findByPrimaryKey(Object obj_id) {
        return dao.findByPrimaryKey(obj_id);
    }

    public BabPassStationRecord findLastProcessingByTagName(String tagName) {
        return dao.findLastProcessingByTagName(tagName);
    }

    public List<BabPassStationRecord> findByBab(Bab b) {
        return this.dao.findByBab(b);
    }

    public int insert(BabPassStationRecord pojo) {
        return dao.insert(pojo);
    }

    /**
     * @param bab
     * @param tagName
     * @param barcode
     * @return Number of the barcode input count in database
     */
    public int checkStationInfoAndInsert(Bab bab, String tagName, String barcode) {

        //Check input bab and barcode is correct or not
        List<BabSettingHistory> settings = babSettingService.findByBab(bab);
        BabSettingHistory currentTag = settings.stream()
                .filter(p -> tagName.equals(p.getTagName().getName())).findFirst()
                .orElse(null);

        checkState(currentTag != null, "Can't find processing record");

        Bab userInput = bab;
        Bab b = currentTag.getBab();
        checkState(userInput.getId() == b.getId(), "Processing record id not match");

        List<BabPassStationRecord> barcodeRecords = dao.findByBab(bab);

        List<BabPassStationRecord> barcodeInCurrentTagName = barcodeRecords.stream()
                .filter(p -> tagName.equals(p.getTagName().getName()) && barcode.equals(p.getBarcode()))
                .collect(toList());
        int inputCount = barcodeInCurrentTagName.size() + 1;
        if (b.getIspre() == 0) {
            checkArgument((barcode.length() >= 5), "Barcode length can't under five character!");
            checkBarcodeSn(b, barcode);
            List<BabPassStationRecord> tagBarcodeRecords = this.dao.findByBarcodeAndTagName(barcode, tagName);
            List<BabPassStationRecord> diffBabRecords = (List<BabPassStationRecord>) tagBarcodeRecords.stream().filter(p -> (p.getBab().getId() != bab.getId())).collect(toList());
            checkState(diffBabRecords.isEmpty(), "Barcode input too much times(Exist in other bab record)");
            checkState((inputCount <= 3), "Barcode(" + barcode + ") input too much times");
        }

        //Check barcode after first station
        if (currentTag.getStation() > 1) {
            BabSettingHistory prevTag = settings.stream()
                    .filter(p -> p.getStation() == currentTag.getStation() - 1).findFirst()
                    .orElse(null);
            checkPreviousStation(barcodeRecords, prevTag, barcode);
        }

        int barcodeInput = (int) barcodeRecords.stream()
                .filter(p -> tagName.equals(p.getTagName().getName()))
                .count();

        //Check when current barcode first input
        if (barcodeInput > 0 && barcodeInCurrentTagName.isEmpty()) {
            List<BabPassStationRecord> currentTagNameRecords = barcodeRecords.stream()
                    .filter(p -> tagName.equals(p.getTagName().getName()))
                    .collect(toList());
            checkPreviousBarcode(currentTagNameRecords);
        }

        BabPassStationRecord rec = new BabPassStationRecord();
        rec.setBab(currentTag.getBab());
        rec.setTagName(currentTag.getTagName());
        rec.setBarcode(barcode);
        this.insert(rec);

        return inputCount;
    }

    private void checkPreviousStation(List<BabPassStationRecord> barcodeRecords, BabSettingHistory tag, String barcode) {
        List<BabPassStationRecord> prevBarcodeRecords = barcodeRecords.stream()
                .filter(p -> Objects.equals(p.getTagName(), tag.getTagName()) && barcode.equals(p.getBarcode()))
                .collect(toList());
        checkState(prevBarcodeRecords.size() > 1, "Barcode(" + barcode + ") is not finished yet on " + tag.getTagName().getName());
    }

    //Check previous barcode input size is greater than 2(barcode begin -> end, two records)
    private void checkPreviousBarcode(List<BabPassStationRecord> barcodeRecords) {

        BabPassStationRecord lastInput = barcodeRecords.get(barcodeRecords.size() - 1);

        List<BabPassStationRecord> prevBarcodes = barcodeRecords.stream()
                .filter(p -> Objects.equals(lastInput.getBarcode(), p.getBarcode()))
                .collect(toList());

        checkState(prevBarcodes.size() > 1, "Barcode(" + lastInput.getBarcode() + ") is not finished yet");
    }

    private void checkBarcodeSn(Bab b, String barcode) {
        String po = rv.getPoByBarcode(barcode, Factory.DEFAULT);
        checkState(Objects.equals(po, b.getPo()), "Barcode's SN not match");
    }

    public int update(BabPassStationRecord pojo) {
        return dao.update(pojo);
    }

    public int delete(BabPassStationRecord pojo) {
        return dao.delete(pojo);
    }

}
