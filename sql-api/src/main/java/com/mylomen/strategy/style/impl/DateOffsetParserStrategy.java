package com.mylomen.strategy.style.impl;

import com.mylomen.annotation.Table;
import com.mylomen.annotation.TableInfo;
import com.mylomen.strategy.style.TableInfoParserStrategy;
import com.mylomen.util.ReflectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * 偏移解析策略
 *
 * @author Shaoyongjun
 * @date 2018/1/3 0003
 */
public class DateOffsetParserStrategy implements TableInfoParserStrategy {


    private static final long serialVersionUID = 2748342430285527817L;

    @Override
    public TableInfo parserTableInfo() {
        Assert.isTrue(this.getClass().isAnnotationPresent(Table.class), "The class should use the '@Table' annotation");

        Table annotation = this.getClass().getAnnotation(Table.class);
        String db = StringUtils.isNotEmpty(annotation.db()) ? annotation.db() + "." : "";

        StringBuilder nameSb = new StringBuilder();
        nameSb.append(db);
        nameSb.append(annotation.tabPrefix());
        nameSb.append(annotation.name());

        //获取偏移依据的字段
        final String dateOffsetField = annotation.tabSuffixPolicyProperty();
        final Object fieldValue = ReflectionUtil.getFieldValue(this, dateOffsetField);
        Assert.notNull(fieldValue, "Member variables : " + fieldValue + " is null");
        nameSb.append("_" + fieldValue.toString());

        return new TableInfo(annotation.key(), nameSb.toString());
    }
}
