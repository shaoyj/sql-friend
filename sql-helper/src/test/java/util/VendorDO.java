package util;

import com.alibaba.fastjson.JSON;
import com.mylomen.annotation.Column;
import com.mylomen.annotation.Table;
import com.mylomen.strategy.style.impl.DefaultParserStrategy;
import lombok.Data;

import java.util.Date;

/**
 * @author: Shaoyongjun
 * @date: 2018/6/26
 * @time: 上午9:21
 */
@Data
@Table(name = "test", key = "id")
public class VendorDO extends DefaultParserStrategy {

    private static final long serialVersionUID = 8434829038513865498L;

    @Column(name = "id")
    private Long id;


    @Column(name = "sn")
    private String sn;


    @Column(name = "test_name")
    private String test_name;

    @Column(name = "status")
    private Integer status;

    @Column(name = "test_state")
    private Integer testState;



    /**
     * 创建时间
     */
    @Column(name = "ctime")
    private Date ctime;

    /**
     * 修改时间
     */
    @Column(name = "mtime")
    private Date mtime;

    /**
     * 已删除标识
     */
    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "version")
    private Integer version;



    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


}
