package com.mylomen.annotation;

import java.lang.annotation.*;

/**
 * 数据库table注解
 *
 * @author ShaoYongJun
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    /**
     * 数据库名称
     *
     * @return
     */
    String db() default "";

    /**
     * 表名
     *
     * @return
     */
    String name();

    /**
     * 表的主键,支持多个( 主键之间是以','号分割,末尾也要是,结束)
     *
     * @return
     */
    String key();


    /**
     * 表名前缀
     *
     * @return
     */
    String tabPrefix() default "";

    /**
     * 表名后缀生成策略
     *
     * @return
     */
    String tabSuffixPolicyProperty() default "";


}