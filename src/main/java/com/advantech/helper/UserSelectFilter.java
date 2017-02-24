/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.advantech.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Wei.Cheng
 */
public class UserSelectFilter {

    private List<Map> list;

    public UserSelectFilter setList(List l) {
        list = l;
        return this;
    }

    public UserSelectFilter filterData(Integer[] id) {
        List newList = new ArrayList();
        Set<Integer> values = new HashSet<>(Arrays.asList(id));
        for (Map m : list) {
            Integer objectId = null;
            if (m.containsKey("id")) {
                objectId = (Integer) m.get("id");
            } else {
                //Get the first key of map
                Iterator it2 = m.keySet().iterator();
                while (it2.hasNext()) {
                    objectId = (Integer) m.get(it2.next());
                    break;
                }
            }
            if (objectId != null && values.contains(objectId)) {
                newList.add(m);
                values.remove(objectId);
            }
        }
        this.setList(newList);
        return this;
    }

    public UserSelectFilter filterData(String key, Object value) {
        List newList = new ArrayList();
        for (Map m : list) {
            Object obj;
            if (m.containsKey(key)) {
                obj = m.get(key);
            } else {
                break;
            }
            if (value != null && value.equals(obj)) {
                newList.add(m);
            }
        }
        this.setList(newList);
        return this;
    }

    public UserSelectFilter greaterThan(String key, Integer value) {
        List newList = new ArrayList();
        for (Map m : list) {
            Integer obj;
            if (m.containsKey(key)) {
                obj = (Integer) m.get(key);
            } else {
                break;
            }
            if (obj != null && value != null) {
                if (obj > value) {
                    newList.add(m);
                }
            }
        }
        this.setList(newList);
        return this;
    }

    public UserSelectFilter lessThan(String key, Integer value) {
        List newList = new ArrayList();
        for (Map m : list) {
            Integer obj;
            if (m.containsKey(key)) {
                obj = (Integer) m.get(key);
            } else {
                break;
            }
            if (value != null) {
                if (obj < value) {
                    newList.add(m);
                }
            }
        }
        this.setList(newList);
        return this;
    }

    public List<Map> getList() {
        return list;
    }
}
