/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.test;

import com.advantech.entity.TagNameComparison;
import com.advantech.model.BasicDAO;
import com.advantech.service.BasicService;
import com.google.gson.Gson;
import java.io.File;
import static java.lang.System.out;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author Wei.Cheng
 */
public class TestClass {

    public static void main(String[] args) {

        int x, n;
        for (n = 0, x = -256; x < 0; x = x + x) {
            n = n + 1;
            out.println(x);
        }
        out.printf("x=%d,n=%d", x, n);

//        BasicDAO.dataSourceInit1();
//
//        List<TagNameComparison> tagSettings = BasicService.getTagNameComparisonService().getAll();
//
//        try {
//            JAXBContext jc = JAXBContext.newInstance(Wrapper.class, TagNameComparison.class);
//            Unmarshaller unmarshaller = jc.createUnmarshaller();
//            // Marshal
//            Marshaller marshaller = jc.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
////            marshal(marshaller, tagSettings, "tagSettings");
////
//            List<TagNameComparison> l = unmarshal(unmarshaller, TagNameComparison.class, "D:\\tagSettings.xml");
//
//            for (Object o : l) {
//                out.println(new Gson().toJson(o));
//            }
//        } catch (JAXBException ex) {
//            Logger.getLogger(TestClass.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

//    private static void marshal(Marshaller marshaller, List<?> list, String name)
//            throws JAXBException {
//        QName qName = new QName(name);
//        Wrapper wrapper = new Wrapper(list);
//        JAXBElement<Wrapper> jaxbElement = new JAXBElement<>(qName,
//                Wrapper.class, wrapper);
//        marshaller.marshal(jaxbElement, new File("D://" + name + ".xml"));
//    }
//
//    private static <T> List<T> unmarshal(Unmarshaller unmarshaller,
//            Class<T> clazz, String xmlLocation) throws JAXBException {
//        StreamSource xml = new StreamSource(xmlLocation);
//        Wrapper<T> wrapper = (Wrapper<T>) unmarshaller.unmarshal(xml,
//                Wrapper.class).getValue();
//        return wrapper.getItems();
//    }

}
