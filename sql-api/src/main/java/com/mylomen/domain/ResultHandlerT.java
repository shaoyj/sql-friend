package com.mylomen.domain;


/**
 * @author ShaoYongJun
 */
public class ResultHandlerT<T> extends ResultHandler {

    /**
     *
     */
    private static final long serialVersionUID = -1475269876126879814L;

    public ResultHandlerT() {
        super();
    }


    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public ResultHandler raiseErrMsg(String msg) {
        code = -1;
        message = msg;
        return this;
    }

}
