package com.mylomen.strategy.bo;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author: Shaoyongjun
 * @date: 2018/6/25
 * @time: 下午3:29
 */
public class SqlArgsBO implements Serializable {
    private static final long serialVersionUID = 3868992863902193055L;

    private String sql;

    private Object[] args;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public SqlArgsBO(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }

    public SqlArgsBO(String sql) {
        this.sql = sql;
    }

    public SqlArgsBO() {
    }

    public boolean isErr() {
        if (sql == null || args == null) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
