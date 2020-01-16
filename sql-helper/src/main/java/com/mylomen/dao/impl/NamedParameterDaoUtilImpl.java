package com.mylomen.dao.impl;

import com.mylomen.core.BeanRowMapperHelper;
import com.mylomen.core.JdbcTemplateWrapper;
import com.mylomen.core.NameParamSqlHelper;
import com.mylomen.core.bo.ConditionBO;
import com.mylomen.core.bo.SqlArgsMapBO;
import com.mylomen.core.utils.BeanUtils;
import com.mylomen.dao.DaoUtil;
import com.mylomen.domain.PageView;
import com.mylomen.domain.ResultHandler;
import com.mylomen.strategy.style.TableInfoParserStrategy;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ShaoYongJun
 */
public class NamedParameterDaoUtilImpl implements DaoUtil {

    private static final Logger logger = LoggerFactory.getLogger(NamedParameterDaoUtilImpl.class);


    private JdbcTemplateWrapper jdbcTemplate;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public NamedParameterDaoUtilImpl(JdbcTemplateWrapper jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    @Override
    public <T extends TableInfoParserStrategy> String getTabName(T entity) {
        assert entity != null && entity.parserTableInfo() != null;
        return entity.parserTableInfo().getName();
    }

    @Override
    public <T extends TableInfoParserStrategy> ResultHandler insertEntity(T entity) {
        ResultHandler resultHandler = new ResultHandler();
        int addCount = 0;
        try {
            addCount = namedParameterJdbcTemplate.update(
                    NameParamSqlHelper.getMergeSql(entity, null).getSql(),
                    BeanUtils.beanConversionMap(entity));
        } catch (Exception e) {
            return resultHandler.raiseEx(e);
        }
        if (addCount <= 0) {
            return resultHandler.raiseErrMsg("保存失败");
        }

        return resultHandler;
    }

    @Override
    public <T extends TableInfoParserStrategy, ID extends Serializable> ID insertAndGetKey(T entity) {
        try {
            SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(entity);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(
                    NameParamSqlHelper.getMergeSql(entity, null).getSql(),
                    sqlParameterSource,
                    keyHolder);
            Long k = keyHolder.getKey().longValue();
            return (ID) k;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public <T extends TableInfoParserStrategy> ResultHandler deleteByPrimaryKey(T entity) {
        ResultHandler resultHandler = new ResultHandler();
        int addCount = 0;
        try {
            SqlArgsMapBO sqlMap = NameParamSqlHelper.getDeleteSqlByParams(new ConditionBO(entity));

            addCount = namedParameterJdbcTemplate.update(
                    sqlMap.getSql(),
                    sqlMap.getParamsMap());
        } catch (Exception e) {
            return resultHandler.raiseEx(e);
        }
        if (addCount <= 0) {
            return resultHandler.raiseErrMsg("删除失败");
        }
        return resultHandler;
    }

    @Override
    public <T extends TableInfoParserStrategy> ResultHandler deleteByEntityOrMap(T entity, Map<String, Object> whereMap) {
        ResultHandler resultHandler = new ResultHandler();
        int addCount = 0;
        try {
            SqlArgsMapBO sqlMap = NameParamSqlHelper.getDeleteSqlByParams(new ConditionBO(entity).initWhereMap(whereMap));

            addCount = namedParameterJdbcTemplate.update(
                    sqlMap.getSql(),
                    sqlMap.getParamsMap());
        } catch (Exception e) {
            return resultHandler.raiseEx(e);
        }
        if (addCount <= 0) {
            return resultHandler.raiseErrMsg("删除失败");
        }
        return resultHandler;
    }

    @Override
    public <T extends TableInfoParserStrategy> ResultHandler updateEntityOrMap(T entity, Map<String, Object> whereMap) {
        ResultHandler resultHandler = new ResultHandler();
        int addCount = 0;
        try {
            SqlArgsMapBO sqlMap = NameParamSqlHelper.getUpdateMapOrPKSql(new ConditionBO(entity).initWhereMap(whereMap));

            addCount = namedParameterJdbcTemplate.update(
                    sqlMap.getSql(),
                    sqlMap.getParamsMap());
        } catch (Exception e) {
            return resultHandler.raiseEx(e);
        }
        if (addCount <= 0) {
            return resultHandler.raiseErrMsg("更新失败");
        }
        return resultHandler;
    }

    @Override
    public <T extends TableInfoParserStrategy> ResultHandler updateByPrimaryKey(T entity) {
        ResultHandler resultHandler = new ResultHandler();
        int addCount = 0;
        try {
            SqlArgsMapBO sqlMap = NameParamSqlHelper.getUpdateMapOrPKSql(new ConditionBO(entity));

            addCount = namedParameterJdbcTemplate.update(
                    sqlMap.getSql(),
                    sqlMap.getParamsMap());
        } catch (Exception e) {
            return resultHandler.raiseEx(e);
        }
        if (addCount <= 0) {
            return resultHandler.raiseErrMsg("更新失败");
        }
        return resultHandler;
    }

    @Override
    public <T extends TableInfoParserStrategy> ResultHandler saveOrUpdate(T entity, List<String> pkList) {
        ResultHandler resultHandler = new ResultHandler();
        int addCount = 0;
        try {
            addCount = namedParameterJdbcTemplate.update(
                    NameParamSqlHelper.getMergeSql(entity, pkList).getSql(),
                    BeanUtils.beanConversionMap(entity));
        } catch (Exception e) {
            return resultHandler.raiseEx(e);
        }
        if (addCount <= 0) {
            return resultHandler.raiseErrMsg("merge failed");
        }

        return resultHandler;
    }

    @Override
    public <T extends TableInfoParserStrategy> List<T> queryForList(T entity, Map<String, Object> whereMap) {
        SqlArgsMapBO listSql = null;
        try {
            //listSql
            listSql = NameParamSqlHelper.querySql(
                    new ConditionBO(entity).initWhereMap(whereMap));

            //query
            return cast(namedParameterJdbcTemplate.query(listSql.getSql(),
                    listSql.getParamsMap(),
                    new BeanRowMapperHelper<>(entity.getClass())));
        } catch (Exception e) {
            logger.warn("occur_err_at_queryForList.listSql:{} e", listSql, e);
        }
        return null;
    }

    @Override
    public <T extends TableInfoParserStrategy> PageView<T> queryForPage(T entity, Map<String, Object> whereMap, PageView<T> pageView) {
        if (pageView == null) {
            pageView = new PageView();
        }
        SqlArgsMapBO listSql = null;
        try {
            //listSql
            listSql = NameParamSqlHelper.querySql(new ConditionBO(entity)
                    .initWhereMap(whereMap)
                    .setPageView(pageView));

            //query
            List<? extends TableInfoParserStrategy> query = namedParameterJdbcTemplate.query(
                    listSql.getSql(),
                    listSql.getParamsMap(),
                    new BeanRowMapperHelper<>(entity.getClass()));
            pageView.setList(cast(query));

            //set count
            Long total = namedParameterJdbcTemplate.queryForObject(
                    "select count(*) from (" + listSql.getSql() + ") temp_count",
                    listSql.getParamsMap(),
                    Long.class
            );
            pageView.setTotalSize(total);
        } catch (Exception e) {
            logger.warn("occur_err_at_queryForList.queryForPage:{} e", listSql, e);
        }


        return pageView;
    }

    @Override
    public <T extends TableInfoParserStrategy> PageView<T> queryForPage(T entity, StringBuilder resultArgs, Map<String, Object> whereMap, PageView<T> pageView) {
        if (pageView == null) {
            pageView = new PageView();
        }
        SqlArgsMapBO listSql = null;
        try {
            //listSql
            listSql = NameParamSqlHelper.querySql(
                    new ConditionBO(entity)
                            .initWhereMap(whereMap)
                            .initResultArgs(resultArgs.toString())
                            .setPageView(pageView));
            //set result
            pageView.setList(cast(namedParameterJdbcTemplate.query(
                    listSql.getSql(),
                    listSql.getParamsMap(),
                    new BeanRowMapperHelper<>(entity.getClass()))));

            //获取 count
            Long total = namedParameterJdbcTemplate.queryForObject(
                    "select count(*) from (" + listSql.getSql() + ") temp",
                    listSql.getParamsMap(),
                    Long.class
            );
            pageView.setTotalSize(total);
        } catch (Exception e) {
            logger.warn("occur_err_at_queryForList.queryForPage:{} e", listSql, e);
        }


        return pageView;
    }

    @Override
    public <T extends TableInfoParserStrategy> Integer queryCountForInt(T entity, Map<String, Object> queryMap) {
        Integer integer = null;
        SqlArgsMapBO totalSql = null;
        try {
            totalSql = NameParamSqlHelper.querySql(
                    new ConditionBO(entity)
                            .initWhereMap(queryMap)
                            .initResultArgs("count(*) as total")
                            .exceptOne());
            integer = namedParameterJdbcTemplate.queryForObject(totalSql.getSql(), totalSql.getParamsMap(), Integer.class);
        } catch (Exception e) {
            logger.warn("occur_err_at_queryCountForInt.totalSql:{} e", totalSql, e);
        }
        return integer;
    }

    @Override
    public <T extends TableInfoParserStrategy> T findEntityByParams(T entity, Map<String, Object> queryMap) {
        SqlArgsMapBO oneSql = NameParamSqlHelper.querySql(new ConditionBO(entity).initWhereMap(queryMap).exceptOne());

        return cast(namedParameterJdbcTemplate.queryForObject(
                oneSql.getSql(),
                oneSql.getParamsMap(),
                new BeanRowMapperHelper<>(entity.getClass())));
    }

    @Override
    public <T extends TableInfoParserStrategy> T findByPrimaryKey(T entity) {
        SqlArgsMapBO oneSql = NameParamSqlHelper.querySql(new ConditionBO(entity).exceptOne());

        return cast(namedParameterJdbcTemplate.queryForObject(
                oneSql.getSql(),
                oneSql.getParamsMap(),
                new BeanRowMapperHelper<>(entity.getClass())));
    }

    @Override
    public <T extends TableInfoParserStrategy> T find(T entity, StringBuilder resultArgs, Map<String, Object> queryMap) {
        SqlArgsMapBO oneSql = NameParamSqlHelper.querySql(
                new ConditionBO(entity)
                        .initWhereMap(queryMap)
                        .initResultArgs(resultArgs.toString())
                        .exceptOne());

        return cast(namedParameterJdbcTemplate.queryForObject(
                oneSql.getSql(),
                oneSql.getParamsMap(),
                new BeanRowMapperHelper<>(entity.getClass())));
    }

    @Override
    public Object executeQuerySql(StringBuilder sql) {
        Object obj = null;
        try {
            obj = namedParameterJdbcTemplate.queryForObject(sql.toString(), new HashMap<>(), Object.class);
        } catch (Exception e) {
            logger.warn("occur_err_at_executeQuerySql.sql:{} e", sql, e);
        }
        return obj;
    }

    @Override
    public Map<String, Object> executeQuerySqlForMap(StringBuilder sql) {
        Object obj = null;
        try {
            obj = namedParameterJdbcTemplate.queryForMap(sql.toString(), new HashMap<>());
        } catch (Exception e) {
            logger.warn("occur_err_at_executeQuerySqlForMap.sql:{} e", sql, e);
        }

        return cast(obj);
    }

    @Override
    public List<Map<String, Object>> executeQuerySqlForList(StringBuilder sql) {
        List<Map<String, Object>> maps = null;
        try {
            maps = namedParameterJdbcTemplate.queryForList(sql.toString(), new HashMap<>());
        } catch (Exception e) {
            logger.warn("occur_err_at_executeQuerySqlForList.sql:{} e", sql, e);
        }
        return maps;
    }

    @Override
    public Object executeSql(StringBuilder sql) {
        try {
            return namedParameterJdbcTemplate.update(sql.toString(), MapUtils.EMPTY_SORTED_MAP);
        } catch (DataAccessException e) {
            logger.warn("occur_err_at_executeSql.sql:{} e", sql, e);
        }

        return null;
    }

    @Override
    public ResultHandler deleteByEntityOrMap(ConditionBO condition) {
        checkCondition(condition);
        ResultHandler resultHandler = new ResultHandler();
        SqlArgsMapBO sqlMap = NameParamSqlHelper.getDeleteSqlByParams(condition);

        int addCount = 0;
        try {
            addCount = namedParameterJdbcTemplate.update(
                    sqlMap.getSql(),
                    sqlMap.getParamsMap());
        } catch (Exception e) {
            return resultHandler.raiseEx(e);
        }
        if (addCount <= 0) {
            return resultHandler.raiseErrMsg("删除失败");
        }
        return resultHandler;
    }

    @Override
    public ResultHandler updateEntityOrMap(ConditionBO condition) {
        checkCondition(condition);
        ResultHandler resultHandler = new ResultHandler();
        SqlArgsMapBO sqlMap = NameParamSqlHelper.getUpdateMapOrPKSql(condition);

        int addCount = 0;
        try {
            addCount = namedParameterJdbcTemplate.update(
                    sqlMap.getSql(),
                    sqlMap.getParamsMap());
        } catch (Exception e) {
            return resultHandler.raiseEx(e);
        }
        if (addCount <= 0) {
            return resultHandler.raiseErrMsg("更新失败");
        }
        return resultHandler;
    }

    @Override
    public <T extends TableInfoParserStrategy> List<T> queryForList(ConditionBO condition) {
        checkCondition(condition);
        Assert.isTrue(condition.exceptList(), "Should be a list query");
        SqlArgsMapBO listSql = NameParamSqlHelper.querySql(condition);
        try {
            return cast(namedParameterJdbcTemplate.query(listSql.getSql(),
                    listSql.getParamsMap(),
                    new BeanRowMapperHelper<>(condition.getEntity().getClass())));
        } catch (Exception e) {
            logger.warn("occur_err_at_queryForList.listSql:{} e", listSql, e);
        }
        return null;
    }

    @Override
    public <T extends TableInfoParserStrategy> PageView<T> queryForPage(ConditionBO condition) {
        checkCondition(condition);
        Assert.isTrue(condition.isPageView(), "Should be a paged query");

        PageView pageView = PageView.newPage(condition.getCurPage(), condition.getPageSize());
        SqlArgsMapBO listSql = NameParamSqlHelper.querySql(condition);

        try {
            List<? extends TableInfoParserStrategy> query = namedParameterJdbcTemplate.query(
                    listSql.getSql(),
                    listSql.getParamsMap(),
                    new BeanRowMapperHelper<>(condition.getEntity().getClass()));
            pageView.setList(cast(query));

            //set count
            Long total = namedParameterJdbcTemplate.queryForObject(
                    "select count(*) from (" + listSql.getSql() + ") temp_count",
                    listSql.getParamsMap(),
                    Long.class
            );
            pageView.setTotalSize(total);
        } catch (Exception e) {
            logger.warn("occur_err_at_queryForList.queryForPage:{} e", listSql, e);
        }
        return pageView;
    }

    @Override
    public Integer queryCountForInt(ConditionBO condition) {
        checkCondition(condition);
        SqlArgsMapBO totalSql = NameParamSqlHelper.querySql(condition.initResultArgs("count(*) as total").exceptOne());

        Integer integer = null;
        try {
            integer = namedParameterJdbcTemplate.queryForObject(
                    totalSql.getSql(),
                    totalSql.getParamsMap(), Integer.class);
        } catch (EmptyResultDataAccessException zeroEx) {
            return 0;
        } catch (Exception e) {
            logger.warn("occur_err_at_queryCountForInt.totalSql:{} e", totalSql, e);
        }
        return integer;
    }

    @Override
    public <T extends TableInfoParserStrategy> T find(ConditionBO condition) {
        checkCondition(condition);
        SqlArgsMapBO oneSql = NameParamSqlHelper.querySql(condition.exceptOne());

        return cast(namedParameterJdbcTemplate.queryForObject(
                oneSql.getSql(),
                oneSql.getParamsMap(),
                new BeanRowMapperHelper<>(condition.getEntity().getClass())));
    }

    @Override
    public Long queryLong(ConditionBO condition) {
        checkCondition(condition);
        SqlArgsMapBO oneSql = NameParamSqlHelper.querySql(condition.exceptOne());
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    oneSql.getSql(),
                    oneSql.getParamsMap(),
                    Long.class
            );
        } catch (EmptyResultDataAccessException zeroEx) {
            return 0L;
        }
    }

    @Override
    public Integer queryInteger(ConditionBO condition) {
        checkCondition(condition);
        SqlArgsMapBO oneSql = NameParamSqlHelper.querySql(condition.exceptOne());
        try {
            return namedParameterJdbcTemplate.queryForObject(
                    oneSql.getSql(),
                    oneSql.getParamsMap(),
                    Integer.class
            );
        } catch (EmptyResultDataAccessException zeroEx) {
            return 0;
        }
    }


    @Override
    public Map<String, Object> queryMap(ConditionBO condition) {
        checkCondition(condition);
        SqlArgsMapBO oneSql = NameParamSqlHelper.querySql(condition.exceptOne());
        return namedParameterJdbcTemplate.queryForMap(
                oneSql.getSql(),
                oneSql.getParamsMap()
        );
    }


    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }


    @Override
    public int[] batchSave(StringBuilder sql, List<?> list) {
        return namedParameterJdbcTemplate.batchUpdate(sql.toString(),
                SqlParameterSourceUtils.createBatch(list.toArray()));
    }

    /**
     * @param condition
     */
    private void checkCondition(ConditionBO condition) {
        Assert.notNull(condition, "condition is null");
        Assert.notNull(condition.getEntity(), "entity is null");
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            return (T) obj;
        } catch (Exception e) {
            logger.warn("occur_err_at cast. obj:{} e", obj, e);
        }
        return null;
    }
}
