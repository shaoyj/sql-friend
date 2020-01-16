package com.mylomen.core.sql;

import com.alibaba.fastjson.JSON;
import com.mylomen.annotation.Column;
import com.mylomen.util.FastJsonUtils;
import lombok.Data;

/**
 * @author: Shaoyongjun
 * @date: 2019/12/5
 * @time: 9:30 PM
 * @copyright by  上海鱼泡泡信息技术有限公司
 */
@Data
public class LiveRoomRankingDO {
    private static final long serialVersionUID = -17518827860767179L;

    @Column(name = "id")
    private Long id;


    /**
     * 打点时间戳。获取每分钟零秒时间戳（精确到秒）
     */
    @Column(name = "dot_time")
    private Long dotTime;

    /**
     * 类目ID
     */
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * 直播房间ID
     */
    @Column(name = "live_room_id")
    private Long liveRoomId;


    /**
     * 直播类型。0:直播；1:直播回放；2:语音直播；3:语音回放
     */
    @Column(name = "live_type")
    private Integer liveType;

    /**
     * 本机主机名
     */
    @Column(name = "host_name")
    private String hostName;


    /**
     * 主播用户ID
     */
    @Column(name = "anchor_id")
    private String anchorId;


    /**
     * 置顶排名
     */
    @Column(name = "top")
    private Integer top;

    /**
     * 总分
     */
    @Column(name = "total_score")
    private Long totalScore;

    /**
     * 直播状态得分
     */
    @Column(name = "live_status_score")
    private Long liveStatusScore;


    /**
     * 消费得分
     */
    @Column(name = "consumer_score")
    private Long consumerScore;

    /**
     * 转化得分
     */
    @Column(name = "conversion_score")
    private Long conversionScore;


    /**
     * 运营管理得分
     */
    @Column(name = "operation_score")
    private Long operationScore;

    public void setTop(Integer top) {
        if (top != null) {
            this.top = top;
        } else {
            this.top = 0;
        }
    }


    public void setTotalScore(Long totalScore) {
        if (totalScore == null) {
            totalScore = 0L;
        }
        this.totalScore = totalScore;
    }


    public String toString() {
        return JSON.toJSONString(this, FastJsonUtils.getSerializeConfig());
    }
}
