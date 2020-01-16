package com.mylomen.dao.impl;

import com.mylomen.annotation.Table;
import com.mylomen.core.JdbcTemplateWrapper;
import com.mylomen.core.utils.MapToObject;
import com.mylomen.core.utils.SqlGenerateProxy;
import com.mylomen.dao.DaoUtil;
import com.mylomen.domain.PageView;
import com.mylomen.domain.ResultHandler;
import com.mylomen.strategy.bo.SqlArgsBO;
import com.mylomen.strategy.style.TableInfoParserStrategy;
import com.mylomen.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 废弃。建议使用 NamedParameterDaoUtilImpl
 * map参数 中in 查询有bug
 *
 * @author ShaoYongJun
 */
@Deprecated
public class DaoUtilImpl extends SqlGenerateProxy implements DaoUtil {

    private static final Logger logger = LoggerFactory.getLogger(DaoUtilImpl.class);


    private JdbcTemplateWrapper jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DaoUtilImpl(JdbcTemplateWrapper jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }


    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * 保存数据
     *
     * @param entity
     * @return
     */
    @Override
    public <T extends TableInfoParserStrategy> ResultHandler insertEntity(T entity) {
        ResultHandler result = new ResultHandler();
        final SqlArgsBO sqlArgs = getInsertSql(entity);
        //debug
        logger.debug("sqlArgs :{}", sqlArgs);

        int row = 0;
        try {
            row = jdbcTemplate.update(sqlArgs.getSql(), sqlArgs.getArgs());
            if (row != 1) {
                result.setCode(-1);
                result.setMessage("insert failed.");
            }
        } catch (RuntimeException err) {
            result.raise(err);
            return result;
        }

        //结果处理
        if (row <= 0) {
            result.setCode(-1);
            result.setMessage("The number of rows affected is 0.");
        }
        return result;
    }

    /**
     * 插入数据并获取Id (支持int和long)
     *
     * @param entity
     * @return
     */
    @Override
    public <T extends TableInfoParserStrategy, ID extends Serializable> ID insertAndGetKey(T entity) {
        final SqlArgsBO sqlArgs = getInsertSql(entity);

        Object keyId = null;
        if (entity.getClass().isAnnotationPresent(Table.class)) {
            Table annotation = entity.getClass().getAnnotation(Table.class);
            Field id = ReflectionUtil.getField(entity, annotation.key());
            Class<?> type = id.getType();
            if (Long.class == id.getType()) {
                keyId = jdbcTemplate.insertAndGetKeyForCommon(Long.class, sqlArgs.getSql(), sqlArgs.getArgs());
            } else if (Integer.class == type) {
                keyId = jdbcTemplate.insertAndGetKeyForCommon(Integer.class, sqlArgs.getSql(), sqlArgs.getArgs());
            }
        } else {
            keyId = jdbcTemplate.insertAndGetKeyForCommon(entity.getClass(), sqlArgs.getSql(), sqlArgs.getArgs());
        }


        return (ID) keyId;
    }


    /**
     * 根据entity或者map删除数据
     *
     * @param entity
     * @param queryMap
     * @return
     */
    @Override
    public <T extends TableInfoParserStrategy> ResultHandler deleteByEntityOrMap(T entity, Map<String, Object> queryMap) {
        ResultHandler result = new ResultHandler();
        SqlArgsBO sqlArgs = getDeleteSqlByParams(entity, queryMap);
        //debug
        logger.debug("sqlArgs :{}", sqlArgs);

        int count = 0;
        try {
            count = jdbcTemplate.update(sqlArgs.getSql(), sqlArgs.getArgs());
        } catch (RuntimeException e) {
            e.printStackTrace();
            result.raise(e);
            return result;
        }

        //结果处理
        if (count <= 0) {
            result.setCode(-1);
            result.setMessage("delete entity failed.");
        }
        return result;
    }

    /**
     * 根据entity或者map更新数据 ( map为null时根据ID跟新，map部位空根据map更新 )
     *
     * @param entity
     * @param queryMap
     * @return
     */
    @Override
    public <T extends TableInfoParserStrategy> ResultHandler updateEntityOrMap(T entity, Map<String, Object> queryMap) {
        ResultHandler result = new ResultHandler();
        final SqlArgsBO sqlArgs = getUpdateSql(entity, queryMap);
        //debug
        logger.debug("sqlArgs :{}", sqlArgs);

        int updateCount = 0;
        try {
            updateCount = jdbcTemplate.update(sqlArgs.getSql(), sqlArgs.getArgs());
        } catch (RuntimeException e) {
            result.raise(e);
            return result;
        }

        //结果处理
        if (updateCount <= 0) {
            result.setCode(-1);
            result.setMessage("The number of rows affected is 0.");
        }
        return result;
    }

    @Override
    public <T extends TableInfoParserStrategy> ResultHandler updateByPrimaryKey(T entityDO) {
        ResultHandler result = new ResultHandler();
        final SqlArgsBO sqlArgs = getUpdateSql(entityDO, null);
        //debug
        logger.debug("sqlArgs :{}", sqlArgs);

        int updateCount = 0;
        try {
            updateCount = jdbcTemplate.update((sqlArgs.getSql() + " limit 1").intern(), sqlArgs.getArgs());
        } catch (RuntimeException e) {
            result.raise(e);
            return result;
        }

        //结果处理
        if (updateCount <= 0) {
            result.setCode(-1);
            result.setMessage("The number of rows affected is 0.");
        }
        return result;
    }

    @Override
    public <T extends TableInfoParserStrategy> ResultHandler saveOrUpdate(T entity, List<String> pkList) {
        ResultHandler result = new ResultHandler();
        final SqlArgsBO sqlArgs = parseSqlMerge(entity, pkList);
        //debug
        logger.debug("sqlArgs :{}", sqlArgs);

        int updateCount = 0;
        try {
            updateCount = jdbcTemplate.update(sqlArgs.getSql(), sqlArgs.getArgs());
        } catch (RuntimeException e) {
            result.raise(e);
            return result;
        }

        //结果处理
        if (updateCount <= 0) {
            result.setCode(-1);
            result.setMessage("The number of rows affected is 0.");
        }
        return result;
    }


    /**
     * 根据条件查询一条数据
     *
     * @param entity
     * @return
     */
    @Override
    public <T extends TableInfoParserStrategy> T findEntityByParams(T entity, Map<String, Object> queryMap) {
        final SqlArgsBO sqlArgs = getSelectByParamsSql(entity, queryMap);
        //debug
        logger.debug("sqlArgs :{}", sqlArgs);

        Map<String, Object> data = null;
        try {
            data = jdbcTemplate.queryForMap(sqlArgs.getSql(), sqlArgs.getArgs());
        } catch (RuntimeException e) {
            return null;
        }

        Object res = MapToObject.mapToObject(data, entity.getClass());
        return (T) res;
    }

    @Override
    public <T extends TableInfoParserStrategy> T findByPrimaryKey(T entityDO) {
        final SqlArgsBO sqlArgs = getSelectByParamsSql(entityDO, null);
        //debug
        logger.debug("sqlArgs :{}", sqlArgs);

        Map<String, Object> data = null;
        try {
            data = jdbcTemplate.queryForMap(sqlArgs.getSql(), sqlArgs.getArgs());
        } catch (RuntimeException e) {
            return null;
        }

        Object res = MapToObject.mapToObject(data, entityDO.getClass());
        return (T) res;
    }

    @Override
    public <T extends TableInfoParserStrategy> T find(T entityDO, StringBuilder resultArgs, Map<String, Object> queryMap) {
        final SqlArgsBO sqlArgs = parseSqlArgsForOne(entityDO, resultArgs, queryMap);
        //debug
        logger.debug("sqlArgs :{}", sqlArgs);

        Map<String, Object> data = null;
        try {
            data = jdbcTemplate.queryForMap(sqlArgs.getSql(), sqlArgs.getArgs());
        } catch (RuntimeException e) {
            return null;
        }

        Object res = MapToObject.mapToObject(data, entityDO.getClass());
        return (T) res;
    }

    /**
     * 根据条件查询总数
     *
     * @param entity
     * @param queryMap
     * @return
     */
    @Override
    public <T extends TableInfoParserStrategy> Integer queryCountForInt(T entity, Map<String, Object> queryMap) {
        SqlArgsBO countArgs = getCountSql(entity, queryMap);

        Integer count = jdbcTemplate.queryForInt(countArgs.getSql(), countArgs.getArgs());

        return count;
    }

    /**
     * 分页查询
     *
     * @param entity
     * @param queryMap
     * @param pageView
     * @return
     */
    @Override
    public <T extends TableInfoParserStrategy> PageView<T> queryForPage(T entity, Map<String, Object> queryMap, PageView<T> pageView) {
        return queryForPage(entity, null, queryMap, pageView);
    }

    @Override
    public <T extends TableInfoParserStrategy> PageView<T> queryForPage(T entity, StringBuilder resultArgs, Map<String, Object> queryMap, PageView<T> pageView) {
        //如果为空就新建
        if (pageView == null) {
            pageView = new PageView();
        }
        final SqlArgsBO sqlArgs = parsePageViewSql(entity, resultArgs, queryMap, pageView);
        SqlArgsBO countSqlArgs = getCountSql(entity, queryMap);

        //判断totalCount是否为0.避免执行后面的分页语句
        int totalCount = jdbcTemplate.queryForInt(countSqlArgs.getSql(), countSqlArgs.getArgs());
        List<Map<String, Object>> list;
        List dataList = null;
        if (totalCount != 0) {
            list = jdbcTemplate.queryForList(sqlArgs.getSql(), sqlArgs.getArgs());
            dataList = new ArrayList<>();
            for (Map<String, Object> map : list) {
                dataList.add(MapToObject.mapToObject(map, (entity.getClass())));
            }
        }

        //装填
        pageView.setList(dataList);
        pageView.setTotalSize(totalCount);
        return pageView;
    }

    /**
     * 根据条件查询一个集合(有风险)
     *
     * @param entity
     * @param queryMap
     * @param
     * @return
     */
    @Override
    public <T extends TableInfoParserStrategy> List<T> queryForList(T entity, Map<String, Object> queryMap) {
        final SqlArgsBO sqlArgs = getSelectPageViewSql(entity, queryMap, null);
        //debug
        logger.debug("sqlArgs: {}", sqlArgs);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlArgs.getSql(), sqlArgs.getArgs());
        List dataList = new ArrayList<>(list.size());
        for (Map<String, Object> map : list) {
            dataList.add(MapToObject.mapToObject(map, (entity.getClass())));
        }
        return dataList;
    }


    @Override
    public <T extends TableInfoParserStrategy> ResultHandler deleteByPrimaryKey(T entityDO) {
        ResultHandler result = new ResultHandler();
        SqlArgsBO sqlArgs = getDeleteSqlByParams(entityDO, null);
        int count = 0;
        try {
            count = jdbcTemplate.update((sqlArgs.getSql() + " limit 1").intern(), sqlArgs.getArgs());
        } catch (RuntimeException e) {
            e.printStackTrace();
            result.raise(e);
            return result;
        }

        //结果处理
        if (count <= 0) {
            result.setCode(-1);
            result.setMessage("delete entity failed.");
        }
        return result;
    }


    @Override
    public Object executeQuerySql(StringBuilder sql) {

        Object obj = null;
        try {
            obj = jdbcTemplate.queryForObject(sql.toString(), Object.class);
        } catch (Exception e) {
            logger.warn("occur_err_at_executeQuerySql.sql:{} e", sql, e);
        }

        return obj;
    }

    @Override
    public Map<String, Object> executeQuerySqlForMap(StringBuilder sql) {

        Map<String, Object> stringObjectMap = null;
        try {
            stringObjectMap = jdbcTemplate.queryForMap(sql.toString());
        } catch (DataAccessException e) {
            logger.warn("occur_err_at_executeQuerySqlForMap.sql:{} e", sql, e);
        }

        return stringObjectMap;
    }

    @Override
    public List<Map<String, Object>> executeQuerySqlForList(StringBuilder sql) {

        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString());

        return maps;
    }

    @Override
    public Object executeSql(StringBuilder sql) {
        try {
            return jdbcTemplate.update(sql.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
