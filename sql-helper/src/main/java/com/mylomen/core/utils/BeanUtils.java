package com.mylomen.core.utils;

import com.mylomen.annotation.Column;
import com.mylomen.util.ReflectionUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/11
 * @time: 2:40 PM
 */
public class BeanUtils {

    /**
     * 将 bean 根据注解转译成键值对，忽略 NULL值（方便数据库操作）
     * key-->数据表列名
     * value-->obj 非空的成员变量数值
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> beanConversionMap(Object obj) {
        Map<String, Object> map = new HashMap<>(16);
        Field[] sunFields = obj.getClass().getDeclaredFields();
        //查找子类
        fillNonNullValues(obj, map, sunFields);

        //父类
        if (obj.getClass() != Object.class && obj.getClass().getSuperclass() != Object.class) {
            Field[] parentFields = obj.getClass().getSuperclass().getDeclaredFields();
            fillNonNullValues(obj, map, parentFields);
        }

        return map;
    }


    /**
     * 装填非空值到map中
     *
     * @param obj
     * @param map
     * @param fields
     */
    private static void fillNonNullValues(Object obj, Map<String, Object> map, Field[] fields) {
        if (ArrayUtils.isNotEmpty(fields)) {
            //遍历
            Arrays.stream(fields)
                    .forEach(field -> {
                        if (field.isAnnotationPresent(Column.class)) {
                            final Object value = ReflectionUtil.getFieldValue(obj, field.getName());
                            if (Objects.nonNull(value)) {
                                map.put(field.getAnnotation(Column.class).name(), value);
                            }
                        }
                    });
        }
    }


}
