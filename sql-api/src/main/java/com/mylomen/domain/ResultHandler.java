package com.mylomen.domain;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author ShaoYongJun
 */
public class ResultHandler implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResultHandler.class);
    private static final long serialVersionUID = 3331105527148782624L;
    protected int code;
    protected String message = "";

    public ResultHandler() {
        this.code = 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void raise(Exception ex) {
        if (LOGGER.isDebugEnabled()) {
            ex.printStackTrace();
        }
        code = -1;
        message = ex.getMessage();
    }


    public ResultHandler raiseErrMsg(String msg) {
        code = -1;
        message = msg;
        return this;
    }


    public ResultHandler raiseEx(Exception ex) {
        code = -1;

        if (LOGGER.isDebugEnabled()) {
            ex.printStackTrace();
        }
        message = ex.getMessage();
        return this;
    }

    /**
     * 是否成功
     *
     * @return
     */
    public boolean hasSuccess() {
        return this.code == 0;
    }

    /**
     * 是否失败
     *
     * @return
     */
    public boolean hasFailed() {
        return this.code != 0;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
