package com.jychan.tools.limiter.adapter;

import com.jychan.tools.limiter.base.BaseLimiter;
import com.jychan.tools.limiter.base.LimiterKV;
import com.jychan.tools.limiter.base.LimiterKvUtils;
import com.jychan.tools.limiter.base.LimiterResult;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 随机限流器
 *
 * Created by chenjinying on 2018/3/10.
 * mail: 415683089@qq.com
 */
@Component
public class LimiterRandomAdapter extends BaseLimiter {

    private static Logger logger = Logger.getLogger(LimiterRandomAdapter.class);

    @Override
    public LimiterResult isPass(String packageName, String method, String returnValue, Class<?> returnType, LimiterKV[] limiterKVs, Object[] args) {
        try {
            // 提取配置中的"rate"
            double rate = LimiterKvUtils.getValDouble(limiterKVs, "rate", 1.0);
            double random = Math.random();
            if (rate > random) {
                return new LimiterResult(true, null);
            } else {
                return new LimiterResult(false, returnRejectObject(returnValue, returnType));
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            // 异常放行不影响业务流程
            return new LimiterResult(true, null);
        }
    }
}
