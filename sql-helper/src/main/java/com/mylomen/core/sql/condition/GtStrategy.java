package com.mylomen.core.sql.condition;

import com.mylomen.core.consts.ConditionConstant;
import com.mylomen.core.sql.SqlConvertStrategy;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/10
 * @time: 6:21 PM
 */
public class GtStrategy implements SqlConvertStrategy {

    @Override
    public boolean filter(String conditionFlag) {
        return StringUtils.contains(ConditionConstant.GT, conditionFlag);
    }

    @Override
    public void wrapperSql(String columnName, StringBuilder sql) {
        sql.append(" AND ").append(columnName)
                .append(" >:")
                .append(columnName).append(ConditionConstant.GT);
    }


}
