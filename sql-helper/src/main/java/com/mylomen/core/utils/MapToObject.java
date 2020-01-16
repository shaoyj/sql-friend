package com.mylomen.core.utils;

import com.mylomen.core.cache.EntityCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Copyright (c) 2017/1/20 by ShaoYongJun
 */
@Deprecated
public class MapToObject {


    private static final Logger logger = LoggerFactory.getLogger(MapToObject.class);


    /**
     * 解析对象
     *
     * @param map       从数据库查询得到的数据
     * @param beanClass
     * @return
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) {
        if (map == null || beanClass == null) {
            return null;
        }

        Map<String, String> beanNameMap = EntityCache.beanNameConversion(beanClass);
        Object obj;
        try {
            obj = beanClass.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass(), Object.class);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                Method setter = property.getWriteMethod();
                if (setter != null) {
                    Object value = map.getOrDefault(beanNameMap.get(property.getName()), null);
                    if (value != null) {
                        //1. 类型不相等
                        if ((property.getPropertyType() != value.getClass())) {
                            String valueStr = value.toString();
                            if (Boolean.class.equals(value.getClass())) {
                                Boolean value1 = (Boolean) value;
                                valueStr = value1.booleanValue() == true ? "1" : "0";
                                setter.invoke(obj, valueStr);
                                continue;
                            } else if (java.sql.Timestamp.class.equals(value.getClass()) && property.getPropertyType() == Long.class) {
                                Date timeData = (Date) value;
                                setter.invoke(obj, timeData.getTime());
                                continue;
                            }

                            Object result = null;
                            //1.1 针对各种类型
                            if (property.getPropertyType() == String.class) {
                                result = valueStr;
                            } else if (Long.class.equals(property.getPropertyType())) {
                                result = new BigDecimal(valueStr).longValue();
                            } else if (Integer.class.equals(property.getPropertyType())) {
                                result = new BigDecimal(valueStr).intValue();
                            } else if (Double.class.equals(property.getPropertyType())) {
                                result = new BigDecimal(valueStr).doubleValue();
                            } else if (Float.class.equals(property.getPropertyType())) {
                                result = new BigDecimal(valueStr).floatValue();
                            } else if (Byte.class.equals(property.getPropertyType())) {
                                result = new BigDecimal(valueStr).byteValue();
                            } else if (BigDecimal.class.equals(property.getPropertyType())) {
                                result = new BigDecimal(valueStr);
                            } else {
                                //日期 等类型
                                setter.invoke(obj, value);
                                continue;
                            }
                            setter.invoke(obj, result);
                        } else {
                            setter.invoke(obj, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }


}
