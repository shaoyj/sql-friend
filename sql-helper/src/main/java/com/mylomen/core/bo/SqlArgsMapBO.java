package com.mylomen.core.bo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/10
 * @time: 10:50 AM
 */
@Data
public class SqlArgsMapBO implements Serializable {
    private static final long serialVersionUID = 8368470035816905758L;


    private String sql;

    private Map<String, Object> paramsMap;


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
