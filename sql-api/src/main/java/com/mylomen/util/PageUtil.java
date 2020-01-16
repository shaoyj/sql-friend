package com.mylomen.util;

/**
 * @author: Shaoyongjun
 * @date: 2018/6/26
 * @time: 下午2:19
 */
public class PageUtil {
    /**
     * 处理分页当前页逻辑
     *
     * @param curPage
     * @return
     */
    public static Integer curPageDeal(Integer curPage) {
        if ((curPage == null) || (curPage <= 0)) {
            return new Integer(1);
        }
        return curPage;
    }
    /**
     * 处理分页尺寸逻辑
     *
     * @param pageSize
     * @return
     */
    public static Integer pageSizeDeal(Integer pageSize) {
        return (pageSize == null)
                ? new Integer(10)
                : pageSize;
    }
}
