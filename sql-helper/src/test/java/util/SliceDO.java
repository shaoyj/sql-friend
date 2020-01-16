package util;

import com.alibaba.fastjson.JSON;
import com.mylomen.annotation.Column;
import com.mylomen.annotation.Table;
import com.mylomen.strategy.style.impl.DefaultParserStrategy;
import lombok.Data;

import java.sql.Blob;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/12
 * @time: 2:09 PM
 */
@Data
@Table(name = "slice", key = "id")
public class SliceDO extends DefaultParserStrategy {

    private static final long serialVersionUID = 4706177353618552804L;

    @Column(name = "id")
    private String id;

    @Column(name = "targeting")
    private Blob targeting;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


}
