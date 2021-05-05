///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// * 取得資料庫資訊用
// */
//package com.advantech.quartzJob;
//
//import com.advantech.facade.BabLineTypeFacade;
//import com.advantech.facade.TestLineTypeFacade;
//import com.advantech.model.db1.TagNameComparison;
//import com.advantech.service.db1.TagNameComparisonService;
//import java.util.List;
//import javax.annotation.PostConstruct;
//import org.json.JSONArray;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// *
// * @author Wei.Cheng
// */
////@Component
//public class TagValueChange {
//
//    private static final Logger log = LoggerFactory.getLogger(TagValueChange.class);
//
//    @Autowired
//    private TestLineTypeFacade tF;
//
//    @Autowired
//    private BabLineTypeFacade bF;
//
//    @Autowired
//    private TagNameComparisonService tagSettingService;
//    
//    @Autowired
//    private WebAccessRESTful webAccessPort;
//    
//    private List<TagNameComparison> tagSettings;
//    
//    @PostConstruct
//    private void init(){
//        tagSettings = tagSettingService.findAll();
//    }
//
//    public String getData() {
//        
//        return new JSONArray().put(tF.getJSONObject())
//                .put(bF.getJSONObject()).toString();
//    }
//
//    public void execute() throws Exception {
//        
//    }
//}
