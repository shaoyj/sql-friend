package com.mylomen.util;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;

/**
 * @author: Shaoyongjun
 * @date: 2019/3/12
 * @time: 下午2:41
 */
public class FastJsonUtils {


    private static SerializeConfig config = new SerializeConfig();

    private static ParserConfig parserConfig = new ParserConfig();

    /**
     * FAST json 设置
     */
    static {
        // 序列化配置对象
        config.propertyNamingStrategy = com.alibaba.fastjson.PropertyNamingStrategy.SnakeCase;

        // 反序列化配置对象
        parserConfig.propertyNamingStrategy = com.alibaba.fastjson.PropertyNamingStrategy.SnakeCase;
    }


    public static SerializeConfig getSerializeConfig() {
        return config;
    }


    public static ParserConfig getParserConfig() {
        return parserConfig;
    }
}
