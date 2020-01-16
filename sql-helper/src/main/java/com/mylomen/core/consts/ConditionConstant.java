package com.mylomen.core.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/10
 * @time: 3:06 PM
 */
public interface ConditionConstant {

    String CONDITION_SEPARATOR = "__";

    String LE = "__le";
    String LT = "__lt";
    String GE = "__ge";
    String GT = "__gt";
    String NE = "__ne";

    String LIKE = "__like";
    String IN = "__in";
    String NOT_IN = "__not_in";

    /**
     * 兼容
     */
    Map<String, String> COMPATIBLE_MAP = new HashMap<String, String>(8) {
        private static final long serialVersionUID = -8431483863620458447L;

        {
            put(">=", GE);
            put(">", GT);
            put("<=", LE);
            put("<", LT);
            put("!=", NE);
        }
    };

}
