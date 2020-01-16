package com.mylomen.strategy.style.impl;

import com.mylomen.annotation.Table;
import com.mylomen.annotation.TableInfo;
import com.mylomen.strategy.style.TableInfoParserStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * 默认解析策略
 *
 * @author ShaoYongJun
 */
public class DefaultParserStrategy implements TableInfoParserStrategy {


    private static final long serialVersionUID = 339680612424452929L;

    @Override
    public TableInfo parserTableInfo() {
        Assert.isTrue(this.getClass().isAnnotationPresent(Table.class), "The class should use the '@Table' annotation");

        Table annotation = this.getClass().getAnnotation(Table.class);
        String db = StringUtils.isNotEmpty(annotation.db()) ? annotation.db() + "." : "";

        return new TableInfo(annotation.key(), db + annotation.tabPrefix() + annotation.name());
    }
}
