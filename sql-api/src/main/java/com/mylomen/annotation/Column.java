package com.mylomen.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    /**
     * 对应的表的列名字
     *
     * @return
     */
    String name();

}