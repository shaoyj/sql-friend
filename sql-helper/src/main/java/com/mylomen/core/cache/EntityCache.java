package com.mylomen.core.cache;

import com.mylomen.annotation.Column;
import com.mylomen.annotation.TableInfo;
import com.mylomen.strategy.style.TableInfoParserStrategy;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/10
 * @time: 10:22 AM
 */
public class EntityCache {

    /**
     * 缓存bean和数据表的键列对
     */
    private static Map<Class<?>, Map<String, String>> beanNameToTableColumnMap = new HashMap<>(64);

    /**
     * 基于注解的bean成员变量名称转译
     * key-->拥有@Column注解的 成员变量名称
     * value-->数据表列名
     *
     * @param clazz
     * @return
     */
    public static Map<String, String> beanNameConversion(Class<?> clazz) {
        //先访问bean缓存
        Map<String, String> beanNameConversionByClazz = beanNameToTableColumnMap.get(clazz);
        if (beanNameConversionByClazz != null) {
            return beanNameConversionByClazz;
        }

        //正常逻辑
        Map<String, String> map = new HashMap<>();
        Field[] objFields = clazz.getDeclaredFields();
        //查找子类
        if (ArrayUtils.isNotEmpty(objFields)) {
            //遍历
            Arrays.stream(objFields)
                    .filter(Objects::nonNull)
                    .forEach(field -> {
                        if (field.isAnnotationPresent(Column.class)) {
                            map.put(field.getName(), field.getAnnotation(Column.class).name());
                        }
                    });
        }
        //父类
        if (clazz != Object.class && clazz.getSuperclass() != Object.class) {
            Field[] parentFileds = clazz.getSuperclass().getDeclaredFields();
            if (ArrayUtils.isNotEmpty(parentFileds)) {
                //遍历
                Arrays.stream(parentFileds)
                        .filter(Objects::nonNull)
                        .forEach(objFile -> {
                            if (objFile.isAnnotationPresent(Column.class)) {
                                map.put(objFile.getName(), objFile.getAnnotation(Column.class).name());
                            }
                        });
            }
        }

        //存入bean缓存
        beanNameToTableColumnMap.put(clazz, map);
        return map;
    }


    private static final Map<Class<?>, List<Field>> cacheFieldListMap = new HashMap<>(64);

    /**
     * 获取clazz成员变量中 拥有@Column注解的 Field 列表
     *
     * @param clazz
     * @return
     */
    public static List<Field> getAnnotationFieldList(Class<?> clazz) {
        List<Field> cacheList = cacheFieldListMap.get(clazz);
        if (cacheList != null) {
            return cacheList;
        }


        List<Field> list = new ArrayList<>();

        Field[] entityFields = clazz.getDeclaredFields();
        if (ArrayUtils.isNotEmpty(entityFields)) {
            Arrays.stream(entityFields).forEach(field -> {
                if (field.isAnnotationPresent(Column.class)) {
                    list.add(field);
                }
            });
        }

        //父类
        if (clazz != Object.class && clazz.getSuperclass() != Object.class) {
            Field[] parentFileds = clazz.getSuperclass().getDeclaredFields();
            if (ArrayUtils.isNotEmpty(parentFileds)) {
                //遍历
                Arrays.stream(parentFileds).forEach(objFile -> {
                    if (objFile.isAnnotationPresent(Column.class)) {
                        list.add(objFile);
                    }
                });
            }
        }


        //放入缓存
        cacheFieldListMap.put(clazz, list);
        return list;
    }


    /**
     * 解析表信息
     *
     * @param entityDO
     * @return
     */
    public static TableInfo getTableInfo(TableInfoParserStrategy entityDO) {
        return entityDO.parserTableInfo();
    }


    private static Map<Class<?>, String> resultMap = new HashMap<>(64);

    /**
     * 根据类的注解获取解析SQL参数
     *
     * @param clazz
     * @return
     */
    public static String getResultListByClass(Class<?> clazz) {
        //先走缓存
        String resultMapByClass = resultMap.get(clazz);
        if (StringUtils.isNoneBlank(resultMapByClass)) {
            return resultMapByClass;
        }

        StringBuilder resultList = new StringBuilder();

        //正常逻辑
        Field[] objFields = clazz.getDeclaredFields();
        //查找子类
        if (ArrayUtils.isNotEmpty(objFields)) {
            //遍历
            Arrays.stream(objFields)
                    .forEach(objFile -> {
                        if (objFile.isAnnotationPresent(Column.class)) {
                            resultList.append(objFile.getAnnotation(Column.class).name()).append(" ,");
                        }
                    });
        }
        //查找父类
        if (clazz != Object.class && clazz.getSuperclass() != Object.class) {
            Field[] parentFileds = clazz.getSuperclass().getDeclaredFields();
            if (ArrayUtils.isNotEmpty(parentFileds)) {
                //遍历
                Arrays.stream(parentFileds)
                        .forEach(objFile -> {
                            if (objFile.isAnnotationPresent(Column.class)) {
                                resultList.append(objFile.getAnnotation(Column.class).name()).append(" ,");
                            }
                        });
            }
        }

        //如果没有注解则使用 *
        if (resultList.length() == 0) {
            resultList.append(" * ");
        }
        StringBuilder stringBuilder = resultList.deleteCharAt(resultList.length() - 1).append(" ");
        resultMap.put(clazz, stringBuilder.toString());
        return stringBuilder.toString();
    }


}
