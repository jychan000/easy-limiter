package com.jychan.tools.limiter.adapter;

import com.jychan.tools.limiter.base.BaseLimiter;
import com.jychan.tools.limiter.base.LimiterKV;
import com.jychan.tools.limiter.base.LimiterKvUtils;
import com.jychan.tools.limiter.base.LimiterResult;
import com.revinate.guava.util.concurrent.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 总量限流器
 *
 * Created by chenjinying on 2018/3/5.
 * mail: 415683089@qq.com
 */
@Component
public class LimiterTotalAdapter extends BaseLimiter {

    private static Logger logger = Logger.getLogger(LimiterTotalAdapter.class);

    public static final String KEY_RETURN_VAL = "returnVal";
    public static final String KEY_TPS = "tps";

    private Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    public LimiterTotalAdapter() {
        // 先初始化所有默认返回 obj 不用每次失败后生成
    }

    @Override
    public LimiterResult isPass(String packageName, String methodName, String returnValue, Class<?> returnType, LimiterKV[] limiterKVs, Object[] args) {
        try {
            int tps = LimiterKvUtils.getValInt(limiterKVs, KEY_TPS, -1);
            String returnValueTmp = LimiterKvUtils.getValString(limiterKVs, KEY_RETURN_VAL, null);
            if (StringUtils.isNotBlank(returnValueTmp)) {
                returnValue = returnValueTmp;
            }

            String functionKey = packageName + "#" + methodName + ",tps=" + tps + ",returnValue=" + returnValue;

            if (tps <= 0) {
                throw new Exception("无效配置，放弃限制, tps=" + tps);
            }

            RateLimiter limiter = rateLimiterMap.get(functionKey);
            if (limiter == null) {
                synchronized (LimiterTotalAdapter.class) {
                    limiter = rateLimiterMap.get(functionKey);
                    if (limiter == null) {
                        limiter = RateLimiter.create(tps);
                        rateLimiterMap.put(functionKey, limiter);
                    }
                }
            }
            boolean isPass = limiter.tryAcquire();
            if (isPass) {
                return new LimiterResult(true, "pass");
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
