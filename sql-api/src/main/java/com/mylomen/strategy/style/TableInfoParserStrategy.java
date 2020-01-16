package com.mylomen.strategy.style;

import com.mylomen.annotation.Table;
import com.mylomen.annotation.TableInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * 解析表信息策略接口
 * Created by Shaoyongjun on 2018/1/3 0003.
 */
public interface TableInfoParserStrategy extends Serializable {


    /**
     * 根据传入的对象解析出对应的表信息
     *
     * @return
     */
    default TableInfo parserTableInfo() {
        StringBuilder nameSb = new StringBuilder();
        Assert.isTrue(this.getClass().isAnnotationPresent(Table.class), "The class should use the '@Table' annotation");

        Table annotation = this.getClass().getAnnotation(Table.class);
        String db = StringUtils.isNotEmpty(annotation.db()) ? annotation.db() + "." : "";
        nameSb.append(db);

        String name = annotation.name();
        nameSb.append(name);

        return new TableInfo(annotation.key(), nameSb.toString());
    }


}
