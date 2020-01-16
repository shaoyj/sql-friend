package com.mylomen.exception;


/**
 * @author ShaoYongJun
 * @date 2018/1/8 0008.
 */
public class ParseTableInfoException extends RuntimeException {

    private static final long serialVersionUID = -1477139741829403565L;

    public ParseTableInfoException(String msg, Throwable cause) {
        super(" Parsing table name failed   [" + msg + "]", cause);
    }

    public ParseTableInfoException(String msg) {
        super(" Parsing table name failed   [" + msg + "]");
    }


}
