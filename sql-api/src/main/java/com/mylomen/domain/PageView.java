package com.mylomen.domain;

import com.alibaba.fastjson.JSON;
import com.mylomen.util.FastJsonUtils;
import com.mylomen.util.PageUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author ShaoYomngJun
 */
@Data
public class PageView<T> implements Serializable {

    private static final long serialVersionUID = -1436613004772386621L;

    private List<T> list = Collections.emptyList();
    /**
     * current page no.
     */
    private int currentPage = 1;
    /**
     * page size
     */
    private int pageSize = 10;
    /**
     * total pages
     */
    private long totalPages;
    /**
     * total record size
     */
    private long totalSize;

    private String url;
    /**
     * 排序属性，目前只在mongodb的查询当中使用
     */
    private List<String> orderBy;

    /**
     * 图标数据
     */
    private String chartData;


    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        if (currentPage > 0) {
            this.currentPage = currentPage;
        }
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
        setTotalPages(this.totalSize % this.pageSize == 0 ?
                this.totalSize / this.pageSize :
                this.totalSize / this.pageSize + 1
        );
    }


    public static <T> PageView<T> newPage(Integer page, Integer pageSize) {
        PageView pageView = new PageView();
        pageView.setCurrentPage(PageUtil.curPageDeal(page));
        pageView.setPageSize(PageUtil.pageSizeDeal(pageSize));
        return pageView;
    }


    public PageView<T> emptyResult() {
        this.totalPages = 0;
        this.totalSize = 0;
        this.list = Collections.emptyList();
        return this;
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this, FastJsonUtils.getSerializeConfig());
    }
}
