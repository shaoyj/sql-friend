package com.mylomen.util;

/**
 * 时间格式枚举类
 * Copyright (c) 2017/7/11 by ShaoYongJun
 */
public class DateEnums {

    public enum Format {

        yyyy("yyyy"),

        yyyy_MM("yyyy-MM"),

        yyyyMM("yyyyMM"),

        yyyy_MM_dd("yyyy-MM-dd"),

        yyyyMMdd("yyyyMMdd"),

        yyyy_MM_dd_HH("yyyy-MM-dd HH"),

        yyyyMMddHH("yyyyMMddHH"),

        yyyyMMdd_HH_MM_SS("yyyyMMdd HH:mm:ss"),

        yyyy_MM_dd_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),

        yyyyMMddHHMMSS("yyyyMMddHHmmss"),

        yyyyMMddHHMM("yyyyMMddHHmm"),

        MMM_dd("MMM dd"),

        yyyy_MM_dd_HH_MM_SS_Z("yyyy-MM-dd HH:mm:ss Z");

        /**
         * 时间格式
         */
        private String sdf;

        public String getSdf() {
            return sdf;
        }

        Format(String sdf) {
            this.sdf = sdf;
        }
    }


}
