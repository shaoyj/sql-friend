package com.mylomen.core.utils;

import com.mylomen.annotation.Column;
import com.mylomen.annotation.TableInfo;
import com.mylomen.core.cache.EntityCache;
import com.mylomen.domain.PageView;
import com.mylomen.exception.ParseSqlException;
import com.mylomen.exception.ParseTableInfoException;
import com.mylomen.strategy.bo.SqlArgsBO;
import com.mylomen.strategy.style.TableInfoParserStrategy;
import com.mylomen.util.ReflectionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author ShaoYongJun
 */
@Deprecated
public class SqlGenerateProxy {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(SqlGenerateProxy.class);


    /**
     * 获取表名
     *
     * @return
     */
    public String getTabName(TableInfoParserStrategy entity) {
        TableInfo info = getTableInfo(entity);
        return info.getName();
    }


    private TableInfo getTableInfo(TableInfoParserStrategy entityDO) {
        return entityDO.parserTableInfo();
    }


    /**
     * 生产保存数据的sql
     *
     * @param entity
     * @return
     */
    public SqlArgsBO getInsertSql(TableInfoParserStrategy entity) {
        if (entity == null) {
            throw new ParseTableInfoException("entity is null");
        }

        SqlArgsBO sqlArgsBO = new SqlArgsBO();
        TableInfo info = getTableInfo(entity);
        //校验注解的表信息
        if (info == null) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Table.").intern());
        }

        //拼装插入语句
        StringBuilder sqlStr = new StringBuilder("INSERT INTO " + info.getName() + "(");
        StringBuilder valueStr = new StringBuilder("  VALUES (");
        List<Object> args = new ArrayList<>();
        //获取注解字段
        List<Field> annotationFieldList = getAnnotationFieldList(entity.getClass());
        if (CollectionUtils.isEmpty(annotationFieldList)) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Column.").intern());
        }

        //遍历
        for (Field field : annotationFieldList) {
            Column annotation = field.getAnnotation(Column.class);
            // 执行get方法返回一个Object
            Object value = ReflectionUtil.getFieldValue(entity, field);
            if (value != null) {
                sqlStr.append(annotation.name().trim() + ",");
                valueStr.append("?,");
                args.add(value);
            }
        }

        //组装返回值
        sqlArgsBO.setSql((sqlStr.deleteCharAt(sqlStr.length() - 1) + ")" + valueStr.deleteCharAt(valueStr.length() - 1) + ")").intern());
        sqlArgsBO.setArgs(args.toArray());
        return sqlArgsBO;
    }

    public SqlArgsBO parseSqlMerge(TableInfoParserStrategy entity, List<String> pkList) {
        if (entity == null) {
            throw new ParseTableInfoException("entity is null");
        }
        if (CollectionUtils.isEmpty(pkList)) {
            throw new ParseTableInfoException("pkList is empty");
        }

        SqlArgsBO sqlArgsBO = new SqlArgsBO();
        TableInfo info = getTableInfo(entity);
        //校验注解的表信息
        if (info == null) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Table.").intern());
        }

        //拼装插入语句
        StringBuilder sqlStr = new StringBuilder("INSERT INTO " + info.getName() + "(");
        StringBuilder valueStr = new StringBuilder("  VALUES (");
        StringBuilder duplicate = new StringBuilder("  on duplicate key update  ");
        List<Object> args = new ArrayList<>();
        List<Object> duplicateArgs = new ArrayList<>();
        //获取注解字段
        List<Field> annotationFieldList = getAnnotationFieldList(entity.getClass());
        if (CollectionUtils.isEmpty(annotationFieldList)) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Column.").intern());
        }

        //遍历
        for (Field field : annotationFieldList) {
            Column annotation = field.getAnnotation(Column.class);
            // 执行get方法返回一个Object
            Object value = ReflectionUtil.getFieldValue(entity, field);
            if (value != null) {
                final String columnName = annotation.name().trim();
                sqlStr.append(columnName + ",");
                valueStr.append("?,");
                args.add(value);
                //处理 duplicateSqlArgs
                if (pkList.contains(columnName) == false) {
                    duplicate.append(annotation.name().trim() + "=?,");
                    duplicateArgs.add(value);
                }
            }
        }

        //组装返回值
        sqlArgsBO.setSql((sqlStr.deleteCharAt(sqlStr.length() - 1) + ")"
                + valueStr.deleteCharAt(valueStr.length() - 1) + ")"
                + duplicate.deleteCharAt(duplicate.length() - 1)).intern());
        args.addAll(duplicateArgs);
        sqlArgsBO.setArgs(args.toArray());
        return sqlArgsBO;
    }

    /**
     * 根据条件删除数据sql
     *
     * @param entity
     * @param queryMap 为null时根据entity组装删除条件
     * @return
     * @throws Exception
     */
    public SqlArgsBO getDeleteSqlByParams(TableInfoParserStrategy entity, Map<String, Object> queryMap) {
        if (entity == null) {
            throw new ParseTableInfoException("entity is null");
        }

        SqlArgsBO sqlArgs = new SqlArgsBO();
        StringBuilder delSql = new StringBuilder("DELETE FROM ");
        TableInfo info = getTableInfo(entity);
        //校验注解的表信息
        if (info == null) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Table.").intern());
        }

        delSql.append(info.getName() + " WHERE 1 = 1 ");
        //组装条件
        final Object[] args = queryMapOrEntity(entity, queryMap, delSql, true);
        //组装返回语句
        sqlArgs.setSql(delSql.toString());
        sqlArgs.setArgs(args);
        return sqlArgs;
    }

    /**
     * 根据主键/queryMap更新数据sql
     *
     * @param entity
     * @param updateParamsMap
     * @return
     * @throws Exception
     */
    public SqlArgsBO getUpdateSql(TableInfoParserStrategy entity, Map<String, Object> updateParamsMap) {
        if (entity == null) {
            throw new ParseTableInfoException("entity is null");
        }

        SqlArgsBO sqlArgs = new SqlArgsBO();
        List<Object> args = new ArrayList<>();
        //表信息
        TableInfo info = getTableInfo(entity);

        //校验注解的表信息
        if (info == null) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Table.").intern());
        }

        //获取注解字段
        List<Field> annotationFieldList = getAnnotationFieldList(entity.getClass());
        if (CollectionUtils.isEmpty(annotationFieldList)) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Column.").intern());
        }

        //主键们
        final List<String> pkList = Arrays.asList(info.getKey().split(","));
        StringBuilder sqlStr = new StringBuilder("UPDATE " + info.getName() + " SET ");
        //where 语句
        StringBuilder whereStr = new StringBuilder(" WHERE 1=1 ");
        List<Object> whereArgs = new ArrayList<>();

        //遍历
        boolean hasPK = false;
        for (Field field : annotationFieldList) {
            Object value = ReflectionUtil.getFieldValue(entity, field);
            //value 为空不处理
            if (value != null) {
                Column annotation = field.getAnnotation(Column.class);
                //校验
                if (pkList.contains(annotation.name())) {
                    hasPK = true;
                    whereStr.append(" AND ").append(annotation.name()).append("  = ").append("? ");
                    whereArgs.add(value);
                } else {
                    sqlStr.append(annotation.name()).append("  = ").append("?,");
                    args.add(value);
                }
            }
        }

        //判断 更新条件map 是否为空
        if (MapUtils.isNotEmpty(updateParamsMap)) {
            whereStr.replace(0, whereStr.length(), " WHERE 1=1 ");
            args.addAll(Arrays.asList(sqlConversion(updateParamsMap, whereStr, false)));
            sqlArgs.setArgs(args.toArray());
        } else {
            if (hasPK) {
                args.addAll(whereArgs);
            } else {
                throw new ParseSqlException(" Updated SQL condition is empty.");
            }
        }

        //拼装sql和参数
        sqlArgs.setSql(sqlStr.deleteCharAt(sqlStr.length() - 1).append(whereStr).toString());
        sqlArgs.setArgs(args.toArray());
        return sqlArgs;
    }

    /**
     * 计算总数量
     *
     * @param entity
     * @return
     */
    public SqlArgsBO getCountSql(TableInfoParserStrategy entity, Map<String, Object> queryMap) {
        if (entity == null) {
            throw new ParseTableInfoException("entity is null");
        }

        SqlArgsBO sqlArgs = new SqlArgsBO();
        StringBuilder buffer = new StringBuilder("SELECT count(*) FROM ");
        TableInfo info = getTableInfo(entity);
        if (info != null) {
            buffer.append(info.getName() + " WHERE 1 = 1 ");
            Object[] objects = queryMapOrEntity(entity, queryMap, buffer, false);
            sqlArgs.setArgs(objects);
        }

        sqlArgs.setSql(buffer.toString());
        return sqlArgs;
    }

    /**
     * 根据条件查询
     *
     * @param entity
     * @return
     */
    public SqlArgsBO getSelectByParamsSql(TableInfoParserStrategy entity, Map<String, Object> queryMap) {
        return parseSqlArgsForOne(entity, null, queryMap);
    }

    /**
     * 根据条件查询
     *
     * @param entity
     * @return
     */
    public SqlArgsBO parseSqlArgsForOne(TableInfoParserStrategy entity, StringBuilder resultArgs, Map<String, Object> queryMap) {
        if (entity == null) {
            throw new ParseTableInfoException("entity is null");
        }

        SqlArgsBO sqlArgs = new SqlArgsBO();
        StringBuilder buffer = new StringBuilder("SELECT ");
        //判断resultArgs 是否为空
        if (resultArgs == null || resultArgs.length() == 0) {
            buffer.append(EntityCache.getResultListByClass(entity.getClass()));
        } else {
            buffer.append(resultArgs);
        }
        buffer.append(" FROM ");

        TableInfo info = getTableInfo(entity);
        //校验注解的表信息
        if (info == null) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Table.").intern());
        }

        //组装查询语句
        buffer.append(info.getName() + " WHERE 1 = 1 ");
        Object[] objects = null;
        if (MapUtils.isNotEmpty(queryMap)) {
            objects = sqlConversion(queryMap, buffer, false);
        } else {
            objects = sqlConversion(BeanUtils.beanConversionMap(entity), buffer, false);
        }
        buffer.append(" limit 1");


        //组装返回语句
        sqlArgs.setSql(buffer.toString());
        sqlArgs.setArgs(objects);
        return sqlArgs;
    }


    /**
     * @param entity
     * @param resultArgs
     * @param queryMap
     * @param pageView
     * @return
     */
    public SqlArgsBO parsePageViewSql(TableInfoParserStrategy entity, StringBuilder resultArgs, Map<String, Object> queryMap, PageView<?> pageView) {
        if (entity == null) {
            throw new ParseTableInfoException("entity is null");
        }

        SqlArgsBO sqlArgs = new SqlArgsBO();
        StringBuilder buffer = new StringBuilder("SELECT ");
        //判断resultArgs 是否为空
        if (resultArgs == null || resultArgs.length() == 0) {
            buffer.append(EntityCache.getResultListByClass(entity.getClass()));
        } else {
            buffer.append(resultArgs);
        }
        buffer.append(" FROM ");

        TableInfo info = getTableInfo(entity);
        //校验注解的表信息
        if (info == null) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Table.").intern());
        }


        buffer.append(info.getName() + " WHERE 1 = 1 ");
        final Object[] objects = queryMapOrEntity(entity, queryMap, buffer, false);
        sqlArgs.setArgs(objects);

        if (pageView != null) {
            //排序
            if (CollectionUtils.isNotEmpty(pageView.getOrderBy())) {
                buffer.append(" ORDER BY ");
                for (String key : pageView.getOrderBy()) {
                    String[] array = StringUtils.split(key, " ");
                    if (array.length == 2) {
                        buffer.append(array[0] + " " + array[1] + ",");
                    } else {
                        buffer.append(key + ",");
                    }
                }
                buffer.deleteCharAt(buffer.length() - 1);
            }
            //分页
            buffer.append(" limit " + ((pageView.getCurrentPage() - 1) * pageView.getPageSize()) + " , " + pageView.getPageSize());
        }

        sqlArgs.setSql(buffer.toString());
        return sqlArgs;
    }

    /**
     * 分页查询语句
     *
     * @param entity
     * @param queryMap
     * @param pageView
     * @return
     */
    public SqlArgsBO getSelectPageViewSql(TableInfoParserStrategy entity, Map<String, Object> queryMap, PageView<?> pageView) {
        return parsePageViewSql(entity, null, queryMap, pageView);
    }


    /**
     * 拼接条件SQL语句
     *
     * @param queryMap
     * @param buffer
     * @param delFlag  是否是删除语句
     * @return
     */
    public static Object[] sqlConversion(Map<String, Object> queryMap, StringBuilder buffer, boolean delFlag) {
        //校验 如果是删除语句，语句禁止为空
        if (delFlag && MapUtils.isEmpty(queryMap)) {
            throw new ParseSqlException("Deleted sql statement condition is null");
        }

        List<Object> args = new ArrayList<>();
        for (String key : queryMap.keySet()) {
            //组装条件语句
            if (key.endsWith("__like")) {
                buffer.append(" AND " + key.substring(0, key.length() - 6) + " like %?% ");
            } else if (key.endsWith("__>=")) {
                buffer.append(" AND " + key.substring(0, key.length() - 4) + " >= ? ");
            } else if (key.endsWith("__>")) {
                buffer.append(" AND " + key.substring(0, key.length() - 3) + " > ? ");
            } else if (key.endsWith("__<=")) {
                buffer.append(" AND " + key.substring(0, key.length() - 4) + " <= ? ");
            } else if (key.endsWith("__<")) {
                buffer.append(" AND " + key.substring(0, key.length() - 3) + " < ? ");
            } else if (key.endsWith("__in")) {
                buffer.append(" AND " + key.substring(0, key.length() - 4) + " in (?) ");
            } else {
                buffer.append(" AND " + key + " = ? ");
            }

            //组装参数值
            args.add(escapeSql(String.valueOf(queryMap.get(key))));
        }

        return args.toArray();
    }

    /**
     * 获取clazz成员变量 的Field 列表
     *
     * @param clazz
     * @return
     */
    private List<Field> getAnnotationFieldList(Class<?> clazz) {
        List<Field> list = new ArrayList<>();

        Field[] entityFields = clazz.getDeclaredFields();
        if (entityFields != null && entityFields.length > 0) {
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


        return list;
    }

    /**
     * 根据queryMap还是entity查询数据（queryMap优先）
     *
     * @param entity
     * @param queryMap
     * @param buffer
     */
    private Object[] queryMapOrEntity(Object entity, Map<String, Object> queryMap, StringBuilder buffer, boolean delFlag) {
        if (MapUtils.isEmpty(queryMap)) {
            queryMap = BeanUtils.beanConversionMap(entity);
        }

        return sqlConversion(queryMap, buffer, delFlag);
    }

    /**
     * 解决`问题 以及转译符号问题
     *
     * @param str
     * @return
     */
    private static String escapeSql(String str) {
        return StringUtils.replacePattern(str, "\\\\", "\\\\\\\\").replaceAll("\'", "\\\\'");
    }


}
