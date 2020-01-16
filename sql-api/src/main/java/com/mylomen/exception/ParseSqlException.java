package com.mylomen.exception;

/**
 * @author ShaoYongJun
 * @date 2018/1/8 0008.
 */
public class ParseSqlException extends RuntimeException {

    private static final long serialVersionUID = -1477139741829403565L;

    public ParseSqlException(String msg) {
        super(" Parsing Sql failed   [" + msg + "]");
    }
}
