package com.mylomen.util;

import java.lang.reflect.Field;


/**
 * @author ShaoYongJun
 */
public class ReflectionUtil {

    /**
     * 通过get方法获取值
     *
     * @param entity
     * @param field
     * @return
     * @throws Exception
     */
    public static Object getFieldValue(Object entity, Field field) {

        if (field != null) {
            field.setAccessible(true);
        }
        Object value = null;
        try {
            value = field.get(entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;

    }

    public static Object getFieldValue(Object obj, String fieldName) {
        Object result = null;
        Field field = ReflectionUtil.getField(obj, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                result = field.get(obj);
            } catch (Exception e) {
                return "";
            }
        }
        return result;
    }

    public static Field getField(Object obj, String fieldName) {
        Field field = null;
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz
                .getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
            }
        }
        return field;
    }

    public static void setFieldValue(Object obj, String fieldName,
                                     String fieldValue) {
        Field field = ReflectionUtil.getField(obj, fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(obj, fieldValue);
            } catch (Exception e) {

            }
        }
    }


}
