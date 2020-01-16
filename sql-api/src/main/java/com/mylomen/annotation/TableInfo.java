package com.mylomen.annotation;


import java.io.Serializable;

/**
 * @author ShaoYongJun
 */
public class TableInfo implements Serializable {

    private static final long serialVersionUID = -7968990301474424232L;
    private String key;
    private String name;

    public TableInfo(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
