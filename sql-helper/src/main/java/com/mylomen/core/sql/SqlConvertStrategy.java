package com.mylomen.core.sql;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/10
 * @time: 6:18 PM
 */
public interface SqlConvertStrategy {


    /**
     * 过滤
     *
     * @param conditionFlag 条件标示
     * @return
     */
    boolean filter(String conditionFlag);


    /**
     * 根据 列名、条件标示 包装sql
     *
     * @param columnName
     * @param sql
     */
    void wrapperSql(String columnName, StringBuilder sql);

}
