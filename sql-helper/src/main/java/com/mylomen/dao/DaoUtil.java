package com.mylomen.dao;


import com.mylomen.core.bo.ConditionBO;
import com.mylomen.domain.PageView;
import com.mylomen.domain.ResultHandler;
import com.mylomen.strategy.style.TableInfoParserStrategy;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author ShaoYongJun
 */
public interface DaoUtil {


    /**
     * 获取 JdbcTemplate
     *
     * @return
     */
    JdbcTemplate getJdbcTemplate();

    /**
     * 获取 namedParameterJdbcTemplate
     *
     * @return
     */
    NamedParameterJdbcTemplate getNamedParameterJdbcTemplate();

    /**
     * 获取表名
     *
     * @param entity
     * @return tabName
     */
    <T extends TableInfoParserStrategy> String getTabName(T entity);

    /**
     * 根据实体类添加 Success 返回操作记录数
     *
     * @param entity
     * @return
     * @
     */
    <T extends TableInfoParserStrategy> ResultHandler insertEntity(T entity);


    /**
     * 添加：并且回写主键
     *
     * @param entity
     * @return
     * @
     */
    <T extends TableInfoParserStrategy, ID extends Serializable> ID insertAndGetKey(T entity);


    /**
     * 根据entityDO主键删除一条数据
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T extends TableInfoParserStrategy> ResultHandler deleteByPrimaryKey(T entity);

    /**
     * 根据entityDO或者map删除数据
     *
     * @param entity
     * @param queryMap
     * @param <T>
     * @return
     */
    <T extends TableInfoParserStrategy> ResultHandler deleteByEntityOrMap(T entity, Map<String, Object> queryMap);

    /**
     * 根据entityDO或者map更新数据
     *
     * @param entity
     * @param queryMap
     * @return
     */
    <T extends TableInfoParserStrategy> ResultHandler updateEntityOrMap(T entity, Map<String, Object> queryMap);

    /**
     * 根据主键更新数据
     *
     * @param entity
     * @param <T>
     * @return
     */
    <T extends TableInfoParserStrategy> ResultHandler updateByPrimaryKey(T entity);


    /**
     * 保存或者更新数据
     *
     * @param entity
     * @param pkList
     * @param <T>
     * @return
     */
    <T extends TableInfoParserStrategy> ResultHandler saveOrUpdate(T entity, List<String> pkList);

    /**
     * 根据条件查询对象列表
     *
     * @param entity
     * @param queryMap
     * @param <T>
     * @return
     */
    <T extends TableInfoParserStrategy> List<T> queryForList(T entity, Map<String, Object> queryMap);

    /**
     * 单表分页 待条件查询
     *
     * @param entity
     * @param queryMap
     * @param pageView
     * @return
     * @
     */
    <T extends TableInfoParserStrategy> PageView<T> queryForPage(T entity, Map<String, Object> queryMap, PageView<T> pageView);

    /**
     * 分页查询指定的数据
     *
     * @param entity
     * @param resultArgs 指定的返回参数
     * @param queryMap   查询条件参数
     * @param pageView   分页参数
     * @param <T>
     * @return
     */
    <T extends TableInfoParserStrategy> PageView<T> queryForPage(T entity, StringBuilder resultArgs, Map<String, Object> queryMap, PageView<T> pageView);

    /**
     * 带条件获取所有记录：一般用于配合分页
     *
     * @param entity
     * @param queryMap
     * @param <T>
     * @return
     */
    <T extends TableInfoParserStrategy> Integer queryCountForInt(T entity, Map<String, Object> queryMap);


    /**
     * 根据entityDO或者map查询一个对象
     *
     * @param entity
     * @param queryMap
     * @return
     */
    <T extends TableInfoParserStrategy> T findEntityByParams(T entity, Map<String, Object> queryMap);

    /**
     * 根据entityDO或者map查询一个对象
     *
     * @param entity
     * @return
     */
    <T extends TableInfoParserStrategy> T findByPrimaryKey(T entity);


    /**
     * 根据需要查询有限的字段
     *
     * @param entity     实体
     * @param resultArgs 需要返回的字段
     * @param queryMap   查询条件
     * @param <T>
     * @return
     */
    <T extends TableInfoParserStrategy> T find(T entity, StringBuilder resultArgs, Map<String, Object> queryMap);

    /**
     * 执行自定义查询SQL语句 返回一个对象
     *
     * @param sql
     * @return
     */
    Object executeQuerySql(StringBuilder sql);

    /**
     * 执行自定义查询SQL语句 返回一个map
     *
     * @param sql
     * @return
     */
    Map<String, Object> executeQuerySqlForMap(StringBuilder sql);

    /**
     * 执行自定义sql ，返回 list
     *
     * @param sql
     * @return
     */
    List<Map<String, Object>> executeQuerySqlForList(StringBuilder sql);

    /**
     * 执行自定义非查询SQl语句
     *
     * @param sql
     * @return
     */
    Object executeSql(StringBuilder sql);


    /**
     * 根据entityDO或者map删除数据
     *
     * @param condition
     * @return
     */
    default ResultHandler deleteByEntityOrMap(ConditionBO condition) {
        throw new NotImplementedException("不支持");
    }

    /**
     * 根据entityDO或者map更新数据
     *
     * @param condition
     * @return
     */
    default ResultHandler updateEntityOrMap(ConditionBO condition) {
        throw new NotImplementedException("不支持");
    }


    /**
     * 根据条件查询对象列表
     *
     * @param condition
     * @param <T>
     * @return
     */
    default <T extends TableInfoParserStrategy> List<T> queryForList(ConditionBO condition) {
        throw new NotImplementedException("不支持");
    }

    /**
     * 单表分页 待条件查询
     *
     * @param condition
     * @return
     * @
     */
    default <T extends TableInfoParserStrategy> PageView<T> queryForPage(ConditionBO condition) {
        throw new NotImplementedException("不支持");
    }


    /**
     * 带条件获取所有记录：一般用于配合分页
     *
     * @param condition
     * @return
     */
    default Integer queryCountForInt(ConditionBO condition) {
        throw new NotImplementedException("不支持");
    }


    /**
     * 根据entityDO或者map查询一个对象
     *
     * @param condition
     * @return
     */
    default <T extends TableInfoParserStrategy> T find(ConditionBO condition) {
        throw new NotImplementedException("不支持");
    }

    /**
     * 根据entityDO 查询一个Long
     *
     * @param condition
     * @return
     */
    default Long queryLong(ConditionBO condition) {
        throw new NotImplementedException("不支持");
    }


    /**
     * 根据entityDO 查询一个Long
     *
     * @param condition
     * @return
     */
    default Integer queryInteger(ConditionBO condition) {
        throw new NotImplementedException("不支持");
    }


    /**
     * 根据entityDO 查询返回Map
     *
     * @param condition
     * @return
     */
    default Map<String, Object> queryMap(ConditionBO condition) {
        throw new NotImplementedException("不支持");
    }


    /**
     * 批量保存
     *数据库链接 参数rewriteBatchedStatements=true
     * @param sql
     * @param list
     * @return
     */
    default int[] batchSave(StringBuilder sql, List<?> list){
        throw new NotImplementedException("不支持");
    }

}
