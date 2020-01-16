package com.mylomen.core;

import com.mylomen.annotation.Column;
import com.mylomen.annotation.TableInfo;
import com.mylomen.core.bo.ConditionBO;
import com.mylomen.core.bo.SqlArgsMapBO;
import com.mylomen.core.cache.EntityCache;
import com.mylomen.core.consts.ConditionConstant;
import com.mylomen.core.sql.SqlConvertStrategy;
import com.mylomen.core.sql.condition.*;
import com.mylomen.core.utils.BeanUtils;
import com.mylomen.exception.ParseSqlException;
import com.mylomen.exception.ParseTableInfoException;
import com.mylomen.strategy.style.TableInfoParserStrategy;
import com.mylomen.util.ReflectionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/9
 * @time: 6:21 PM
 */
public class NameParamSqlHelper {

    private static final Logger logger = LoggerFactory.getLogger(NameParamSqlHelper.class);


    /**
     * 生成 查询语句
     *
     * @param conditionBO
     * @return
     */
    public static SqlArgsMapBO querySql(ConditionBO conditionBO) {
        assert conditionBO != null;
        TableInfoParserStrategy entity = conditionBO.getEntity();
        TableInfo info = EntityCache.getTableInfo(entity);
        //校验注解的表信息
        if (info == null) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Table.").intern());
        }

        //拼装插入语句
        SqlArgsMapBO argsMapBO = new SqlArgsMapBO();
        StringBuilder findSql = new StringBuilder("SELECT ");

        //判断resultArgs 是否为空
        if (conditionBO.resultArgsBlank()) {
            findSql.append(EntityCache.getResultListByClass(entity.getClass()));
        } else {
            findSql.append(conditionBO.getResultArgs());
        }

        //拼接表名
        findSql.append(" FROM ").append(info.getName()).append(" WHERE 1 = 1 ");
        Map<String, Object> whereMap = conditionBO.getWhereMap();
        //如果 whereMap 为空则解析 entity 参数重的主键
        if (MapUtils.isEmpty(whereMap)) {
            //主键们
            List<String> pkList = Arrays.asList(info.getKey().split(","));
            //
            Map<String, Object> beanMap = BeanUtils.beanConversionMap(entity);
            for (String key : beanMap.keySet()) {
                if (pkList.contains(key)) {
                    whereMap.put(key, beanMap.get(key));
                }
            }
        }


        //解析
        filterConditionConvertSql(whereMap, findSql);
        //group by
        findSql.append(conditionBO.getGroupSql() != null ? conditionBO.getGroupSql() : "");
        //having
        findSql.append(conditionBO.getHavingSql() != null ? conditionBO.getHavingSql() : "");
        //排序
        if (CollectionUtils.isNotEmpty(conditionBO.getOrderBy())) {
            findSql.append(" ORDER BY ");
            for (String key : conditionBO.getOrderBy()) {
                String[] array = StringUtils.split(key, " ");
                if (array.length != 2) {
                    findSql.append(key).append(",");
                } else {
                    findSql.append(array[0]).append(" ").append(array[1]).append(",");
                }
            }
            findSql.deleteCharAt(findSql.length() - 1);
        }
        //limit
        if (conditionBO.isPageView()) {
            //分页
            findSql.append(" limit ")
                    .append((conditionBO.getCurPage() - 1) * conditionBO.getPageSize())
                    .append(" , ")
                    .append(conditionBO.getPageSize());
        } else if (conditionBO.isExceptOne()) {
            if (MapUtils.isEmpty(whereMap)) {
                throw new ParseSqlException("Query no conditions");
            }
            findSql.append(" limit 1 ");
        }

        //校验list查询的 查询条件是否为空
        if (conditionBO.exceptList() && MapUtils.isEmpty(whereMap)) {
            throw new ParseSqlException("Query no conditions, might drag library");
        }


        //设置
        argsMapBO.setParamsMap(whereMap);
        argsMapBO.setSql(findSql.toString());
        logger.debug("argsMapBO:{}", argsMapBO);
        return argsMapBO;
    }


    /**
     * 解析删除语句
     * 注:conditionBO 为空时，entity 主键值不能为空（此时只支持根据主键删除数据）
     *
     * @param conditionBO
     * @return
     */
    public static SqlArgsMapBO getDeleteSqlByParams(ConditionBO conditionBO) {
        assert conditionBO != null;
        TableInfoParserStrategy entity = conditionBO.getEntity();
        TableInfo info = EntityCache.getTableInfo(entity);
        //校验注解的表信息
        if (info == null) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Table.").intern());
        }

        //拼装删除语句
        SqlArgsMapBO argsMapBO = new SqlArgsMapBO();
        StringBuilder delSql = new StringBuilder("DELETE FROM ").append(info.getName()).append(" WHERE 1 = 1 ");
        Map<String, Object> whereMap = conditionBO.getWhereMap();
        //如果 delMap 为空则解析 entity 参数中的主键
        if (MapUtils.isEmpty(whereMap)) {
            //主键们
            List<String> pkList = Arrays.asList(info.getKey().split(","));
            //
            Map<String, Object> beanMap = BeanUtils.beanConversionMap(entity);
            for (String key : beanMap.keySet()) {
                if (pkList.contains(key)) {
                    whereMap.put(key, beanMap.get(key));
                }
            }
            //校验删除条件是否为空
            if (MapUtils.isEmpty(whereMap)) {
                throw new ParseSqlException("Delete statement has no conditions, you might delete all the data.");
            }
        }


        //解析
        filterConditionConvertSql(whereMap, delSql);
        //设置
        argsMapBO.setParamsMap(whereMap);
        argsMapBO.setSql(delSql.toString());
        logger.debug("argsMapBO:{}", argsMapBO);
        return argsMapBO;
    }

    /**
     * 根据 updateMap 或者主键生成更新语句
     * 注意：不支持更新主键
     *
     * @param condition
     * @return
     */
    public static SqlArgsMapBO getUpdateMapOrPKSql(ConditionBO condition) {
        assert condition != null;
        TableInfoParserStrategy entity = condition.getEntity();
        TableInfo info = EntityCache.getTableInfo(entity);
        if (info == null) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Table.").intern());
        }
        //获取注解字段
        List<Field> annotationFieldList = EntityCache.getAnnotationFieldList(entity.getClass());
        if (CollectionUtils.isEmpty(annotationFieldList)) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Column.").intern());
        }

        //拼装插入语句
        SqlArgsMapBO argsMapBO = new SqlArgsMapBO();
        //主键们
        List<String> pkList = Arrays.asList(info.getKey().split(","));
        //SET 语句
        StringBuilder setSql = new StringBuilder("UPDATE " + info.getName() + " SET ");
        //WHERE 语句
        StringBuilder whereStr = new StringBuilder(" WHERE 1=1 ");
        //WHERE 条件
        Map<String, Object> whereMap = condition.getWhereMap();
        //参数条件
        Map<String, Object> paramsMap = new HashMap<>(16);
        //whereMap 是否为空
        boolean whereMapIsEmpty = MapUtils.isEmpty(whereMap);
        //遍历
        for (Field field : annotationFieldList) {
            Object value = ReflectionUtil.getFieldValue(entity, field);
            //value 为空不处理
            if (Objects.nonNull(value)) {
                Column annotation = field.getAnnotation(Column.class);
                //如果 updateMap 为空时，主键作为更新条件
                if (pkList.contains(annotation.name()) && whereMapIsEmpty) {
                    //参数
                    whereMap.put(annotation.name(), value);
                } else {
                    //set 语句
                    setSql.append(annotation.name()).append("  = ")
                            .append(":").append(annotation.name()).append(",");
                    //参数
                    paramsMap.put(annotation.name(), value);
                }
            }
        }

        //检查更新条件是否为空
        if (MapUtils.isEmpty(paramsMap)) {
            throw new ParseSqlException("The field that the update statement needs to update is empty.");
        }
        //校验 更新条件是否为空
        if (MapUtils.isEmpty(whereMap)) {
            throw new ParseSqlException("Update statement has no conditions, you might update all data.");
        }

        //解析
        filterConditionConvertSql(whereMap, whereStr);
        paramsMap.putAll(whereMap);
        //组装返回值
        argsMapBO.setSql(setSql.deleteCharAt(setSql.length() - 1).append(whereStr).toString());
        argsMapBO.setParamsMap(paramsMap);
        logger.debug("argsMapBO:{}", argsMapBO);
        return argsMapBO;
    }


    /**
     * @param entity
     * @param pkList
     * @return
     */
    public static SqlArgsMapBO getMergeSql(TableInfoParserStrategy entity, List<String> pkList) {
        if (entity == null) {
            throw new ParseTableInfoException("entity is null");
        }

        //校验注解的表信息
        TableInfo info = EntityCache.getTableInfo(entity);
        if (info == null) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Table.").intern());
        }

        //拼装插入语句
        SqlArgsMapBO argsMapBO = new SqlArgsMapBO();
        StringBuilder baseSql = new StringBuilder("INSERT INTO " + info.getName() + "(");
        StringBuilder valueStr = new StringBuilder("  VALUES (");
        StringBuilder duplicate = new StringBuilder("  on duplicate key update  ");
        //Waiting for the next version optimization
        Map<String, Object> paramsMap = new HashMap<>(16);
        //获取注解字段
        List<Field> annotationFieldList = EntityCache.getAnnotationFieldList(entity.getClass());
        if (CollectionUtils.isEmpty(annotationFieldList)) {
            throw new ParseSqlException((entity.getClass() + " Missing annotations @Column.").intern());
        }

        //遍历
        boolean hasValue = false;
        for (Field field : annotationFieldList) {
            Column annotation = field.getAnnotation(Column.class);
            // 执行get方法返回一个Object
            Object value = ReflectionUtil.getFieldValue(entity, field);
            if (Objects.nonNull(value)) {
                hasValue = true;
                String columnName = annotation.name().trim();
                //基本SQL
                baseSql.append(annotation.name()).append(",");
                //数值SQL
                valueStr.append(":").append(annotation.name()).append(",");
                //参数
                paramsMap.put(annotation.name(), value);
                //处理 duplicateSqlArgs
                if (pkList != null && !pkList.contains(columnName)) {
                    duplicate.append(columnName).append("=:").append(columnName).append(",");
                }
            }
        }
        if (!hasValue) {
            throw new ParseSqlException((entity.getClass() + " All values are empty").intern());
        }

        //组装返回值
        StringBuilder mergeSql = new StringBuilder(baseSql.deleteCharAt(baseSql.length() - 1)).append(")")
                .append(valueStr.deleteCharAt(valueStr.length() - 1)).append(")");
        if (CollectionUtils.isNotEmpty(pkList)) {
            mergeSql.append(duplicate.deleteCharAt(duplicate.length() - 1));
        }

        //返回
        argsMapBO.setSql(mergeSql.toString());
        logger.debug("argsMapBO:{}", argsMapBO);
        return argsMapBO;
    }


    /**
     * 特殊条件 转换策略集合
     */
    private static List<SqlConvertStrategy> sqlConvertStrategyList = new ArrayList<SqlConvertStrategy>() {
        private static final long serialVersionUID = 4508682003553807386L;

        {
            add(new GeStrategy());
            add(new GtStrategy());
            add(new InStrategy());
            add(new LeStrategy());
            add(new LikeStrategy());
            add(new LtStrategy());
            add(new NeStrategy());
            add(new NotInStrategy());
        }
    };

    /**
     * 过滤条件转换SQL语句
     *
     * @param whereMap
     * @param buffer
     * @return
     */
    private static void filterConditionConvertSql(Map<String, Object> whereMap, StringBuilder buffer) {
        if (MapUtils.isNotEmpty(whereMap)) {
            Map<String, Object> needMergeMap = new HashMap<>(8);
            for (String key : whereMap.keySet()) {
                String[] split = key.split(ConditionConstant.CONDITION_SEPARATOR);
                //不包含 特殊条件标识
                if (split.length == 1) {
                    buffer.append(" AND ").append(key).append("=:").append(key);
                    continue;
                }

                //期望的特殊条件形如 status__ge 或者 status__>=
                if (split.length != 2) {
                    throw new ParseSqlException("'__' 符号非法");
                }

                //获取表字段名称以及 特殊条件标识
                String columnName = split[0];
                String conditionFlag = split[1];

                //特殊条件标识兼容。形如 __ge 兼容 __>=
                String compatibleFlag = ConditionConstant.COMPATIBLE_MAP.get(conditionFlag);
                if (compatibleFlag != null) {
                    conditionFlag = compatibleFlag;
                    //形如  status__>= 组装成 status__ge
                    needMergeMap.put(columnName + compatibleFlag, whereMap.get(key));
                }

                //包装SQL
                String finalConditionFlag = conditionFlag;
                SqlConvertStrategy sqlConvertStrategy = sqlConvertStrategyList.stream()
                        .filter(filterStrategy -> filterStrategy.filter(finalConditionFlag))
                        .findFirst()
                        .orElse(null);
                if (sqlConvertStrategy == null) {
                    throw new ParseSqlException("暂不支持的特殊条件标识： " + conditionFlag);
                }
                sqlConvertStrategy.wrapperSql(columnName, buffer);
            }

            //merge
            whereMap.putAll(needMergeMap);
        }
    }


}
