package com.h9.api.pay.rest.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @Description:
 * @Auther Demon
 * @Date 2017/11/16 19:47 星期四
 */
@JacksonXmlRootElement(localName = "xml")
@XmlRootElement
public class BaseXmlInfo {

    private SortedMap<String, String> map = new TreeMap<>();

    @JsonAnyGetter
    public SortedMap<String, String> get() {
        return map;
    }

    @JsonAnySetter
    public void set(String name, String value) {
        map.put(name, value);
    }

}
