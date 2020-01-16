package com.mylomen.util;

import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2017/2/20 by ShaoYongJun
 *
 */
public class DateFormatUtil {


    private static final ThreadLocal<Map<String, SimpleDateFormat>> cacheThread = new ThreadLocal<>();


    /**
     * 根据传入的时间格式返回 SimpleDateFormat
     *
     * @param format
     * @return
     */
    public static SimpleDateFormat getDateFormat(String format) {
        Assert.notNull(format, "format is null");

        Map<String, SimpleDateFormat> map = cacheThread.get();
        if (map == null) {
            map = new HashMap<>();
            cacheThread.set(map);
        }

        SimpleDateFormat ret = map.get(format);

        if (ret == null) {
            ret = new SimpleDateFormat(format);
            map.put(format, ret);
        }

        return ret;
    }


    /**
     * 根据枚举类型获取 SimpleDateFormat
     *
     * @param format
     * @return
     */
    private static SimpleDateFormat getDateFormat(DateEnums.Format format) {
        return getDateFormat(format.getSdf());
    }

    public static SimpleDateFormat getYear() {
        return getDateFormat(DateEnums.Format.yyyy);
    }

    public static SimpleDateFormat getMonth() {
        return getDateFormat(DateEnums.Format.yyyyMM);
    }

    public static SimpleDateFormat getYMD() {
        return getDateFormat(DateEnums.Format.yyyyMMdd);
    }

    public static SimpleDateFormat getY_M_D() {
        return getDateFormat(DateEnums.Format.yyyy_MM_dd);
    }

    public static SimpleDateFormat getYMD_HMS() {
        return getDateFormat(DateEnums.Format.yyyyMMdd_HH_MM_SS);
    }

    public static SimpleDateFormat getY_M_D_HMS() {
        return getDateFormat(DateEnums.Format.yyyy_MM_dd_HH_MM_SS);
    }

    public static SimpleDateFormat getY_H_D_HH() {
        return getDateFormat(DateEnums.Format.yyyy_MM_dd_HH);
    }

    public static SimpleDateFormat getYHDHH() {
        return getDateFormat(DateEnums.Format.yyyyMMddHH);
    }

    public static SimpleDateFormat getYMDHMS() {
        return getDateFormat(DateEnums.Format.yyyyMMddHHMMSS);
    }


    public static SimpleDateFormat getMD() {
        return getDateFormat(DateEnums.Format.MMM_dd);
    }

    public static SimpleDateFormat getY_M_D_T_HMS_SSS_Z() {
        return getDateFormat(DateEnums.Format.yyyy_MM_dd_HH_MM_SS_Z);
    }


    public static SimpleDateFormat getYMDHHMM() {
        return getDateFormat(DateEnums.Format.yyyyMMddHHMM);
    }


}
