package com.mylomen.strategy.style.impl;

import com.mylomen.annotation.Table;
import com.mylomen.annotation.TableInfo;
import com.mylomen.exception.ParseTableInfoException;
import com.mylomen.strategy.style.TableInfoParserStrategy;
import com.mylomen.util.DateFormatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.time.Instant;

/**
 * 日期解析策略
 *
 * @author Shaoyongjun
 * @date 2018/1/3 0003
 */
public class DateParserStrategy implements TableInfoParserStrategy {


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

        //获取日期格式
        final String dateFormat = annotation.tabSuffixPolicyProperty();
        try {
            final String tabSuffix = DateFormatUtil.getDateFormat(dateFormat).format(Instant.now().toEpochMilli());
            nameSb.append("_" + tabSuffix);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParseTableInfoException("tabSuffix is illegal.dateFormat is :  " + dateFormat, e);
        }

        return new TableInfo(annotation.key(), nameSb.toString());
    }
}
