package com.mylomen.exception;

/**
 * @author ShaoYongJun
 * @date 2019/10/11 0008.
 */
public class AssemblyEntityException extends RuntimeException {

    private static final long serialVersionUID = -1477139741829403565L;

    public AssemblyEntityException(String msg) {
        super("Assembly entity failed [" + msg + "]");
    }
}
