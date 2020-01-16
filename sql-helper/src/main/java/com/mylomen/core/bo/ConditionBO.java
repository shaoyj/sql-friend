package com.mylomen.core.bo;

import com.alibaba.fastjson.JSON;
import com.mylomen.core.consts.ConditionConstant;
import com.mylomen.domain.PageView;
import com.mylomen.strategy.style.TableInfoParserStrategy;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/10
 * @time: 3:04 PM
 */
@Data
public class ConditionBO implements Serializable {
    private static final long serialVersionUID = 2623629044953081798L;

    /**
     * 实体
     */
    private TableInfoParserStrategy entity;

    /**
     * 最终返回的字段
     */
    private String resultArgs;


    /**
     * 最终返回字段是否为空
     *
     * @return
     */
    public boolean resultArgsBlank() {
        return StringUtils.isBlank(resultArgs);
    }

    /**
     * 期望返回一条数据
     */
    private boolean exceptOne;

    private Map<String, Object> whereMap;
    private StringBuilder groupSql;
    private StringBuilder havingSql;
    /**
     * current page no.
     */
    private Integer curPage;
    /**
     * page size
     */
    private Integer pageSize;

    public ConditionBO setPageView(PageView pageView) {
        if (pageView == null) {
            pageView = new PageView();
        }
        this.curPage = pageView.getCurrentPage();
        this.pageSize = pageView.getPageSize();
        return this;
    }

    /**
     * order 条件
     */
    private List<String> orderBy;


    public ConditionBO(TableInfoParserStrategy entity) {
        assert entity != null;

        this.whereMap = new LinkedHashMap<>();
        this.entity = entity;
    }

    public ConditionBO initWhereMap(Map<String, Object> whereMap) {
        if (MapUtils.isNotEmpty(whereMap)) {
            this.whereMap.clear();
            this.whereMap.putAll(whereMap);
        }
        return this;
    }

    public ConditionBO initResultArgs(String resultArgs) {
        if (StringUtils.isNotBlank(resultArgs)) {
            this.resultArgs = resultArgs;
        }
        return this;
    }


    public ConditionBO initGroupSql(String groupCondition) {
        this.groupSql = new StringBuilder(" group by ").append(groupCondition);
        return this;
    }

    public ConditionBO initHavingSql(String havingCondition) {
        this.havingSql = new StringBuilder(" having ").append(havingCondition);
        return this;
    }


    public ConditionBO set(String colName, Object colValue) {
        whereMap.put(colName, colValue);
        return this;
    }


    public ConditionBO setLe(String colName, Object colValue) {
        whereMap.put(colName + ConditionConstant.LE, colValue);
        return this;
    }

    public ConditionBO setLt(String colName, Object colValue) {
        whereMap.put(colName + ConditionConstant.LT, colValue);
        return this;
    }

    /**
     * 大于等于
     *
     * @param colName
     * @param colValue
     * @return
     */
    public ConditionBO setGe(String colName, Object colValue) {
        whereMap.put(colName + ConditionConstant.GE, colValue);
        return this;
    }

    public ConditionBO setGt(String colName, Object colValue) {
        whereMap.put(colName + ConditionConstant.GT, colValue);
        return this;
    }

    public ConditionBO setNe(String colName, Object colValue) {
        whereMap.put(colName + ConditionConstant.NE, colValue);
        return this;
    }

    public ConditionBO setLike(String colName, Object colValue) {
        whereMap.put(colName + ConditionConstant.LIKE, colValue);
        return this;
    }

    public ConditionBO setIn(String colName, Object colValue) {
        whereMap.put(colName + ConditionConstant.IN, colValue);
        return this;
    }

    public ConditionBO setNotIn(String colName, Object colValue) {
        whereMap.put(colName + ConditionConstant.NOT_IN, colValue);
        return this;
    }


    public ConditionBO exceptOne() {
        this.exceptOne = true;
        return this;
    }

    public boolean exceptList() {
        return !isPageView() && !isExceptOne();
    }

    /**
     * 是否是分页查询
     *
     * @return
     */
    public boolean isPageView() {
        return this.getCurPage() != null && this.getPageSize() != null;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
