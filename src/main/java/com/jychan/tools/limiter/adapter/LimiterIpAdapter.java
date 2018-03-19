package com.jychan.tools.limiter.adapter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jychan.tools.limiter.base.*;
import com.jychan.tools.limiter.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 随机限流器
 *
 * Created by chenjinying on 2018/3/10.
 * mail: 415683089@qq.com
 */
@Component
public class LimiterIpAdapter extends BaseLimiter {

    private static Logger logger = Logger.getLogger(LimiterIpAdapter.class);

    public static final String KEY_RETURN_VAL = "returnVal";
    public static final String KEY_TPS = "tps";

    private static final int MAXIMUM_SIZE = 100000;
    private static final int DURATION_ON_SECOND = 3;

    Map<String, LoadingCache<String, KeyCount>> cacheMap = new ConcurrentHashMap<>();

    @Override
    public LimiterResult isPass(String packageName, String method, String returnValue, Class<?> returnType, LimiterKV[] limiterKVs, Object[] args) {
        try {
            int tps = LimiterKvUtils.getValInt(limiterKVs, KEY_TPS, -1);
            String returnValueTmp = LimiterKvUtils.getValString(limiterKVs, KEY_RETURN_VAL, null);
            if (StringUtils.isNotBlank(returnValueTmp)) {
                returnValue = returnValueTmp;
            }

            String functionKey = packageName + "#" + method + ",tps=" + tps + ",rejectReturnVal=" + returnValue;

            if (tps <= 0) {
                logger.error("无效配置，放弃限制, tps=" + tps);
                return new LimiterResult(true, "无效配置，放弃限制, tps=" + tps);
            }

            String ip = getIp(args);
            if (StringUtils.isBlank(ip)) {
                logger.warn("无法获取ip，" + functionKey);
                return new LimiterResult(true, "无法获取ip，" + functionKey);
            }

            LoadingCache<String, KeyCount> cache = getCache(functionKey);
            if (cache == null) {
                logger.error("无法获取缓存，" + functionKey);
                return new LimiterResult(true, "无法获取缓存，" + functionKey);
            }

            String ipKey = ip + "#" + (System.currentTimeMillis() / 1000);
            int count = getCount(cache, ipKey);
            if (count > tps) {
                return new LimiterResult(false, returnRejectObject(returnValue, returnType));
            } else {
                return new LimiterResult(true, null);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            // 异常放行不影响业务流程
            return new LimiterResult(true, null);
        }
    }


    private LoadingCache<String, KeyCount> getCache(String functionKey) {
        LoadingCache<String, KeyCount> cache = cacheMap.get(functionKey);
        if (cache == null) {
            synchronized (LimiterIpAdapter.class) {
                cache = cacheMap.get(functionKey);
                if (cache == null) {
                    cache = CacheBuilder.newBuilder()
                            .maximumSize(MAXIMUM_SIZE)
                            .expireAfterWrite(DURATION_ON_SECOND, TimeUnit.SECONDS)
                            .build(
                                    new CacheLoader<String, KeyCount>() {

                                        @Override
                                        public KeyCount load(String ip) throws Exception {
                                            return new KeyCount(ip, 0);
                                        }
                                    }
                            );
                    // jdk1.7 不可使用 cacheMap.putIfAbsent(functionKey, cache); :(
                    cacheMap.put(functionKey, cache);
                }
            }
        }
        return cache;
    }

    private int getCount(LoadingCache<String, KeyCount> cache, String ipKey) {
        try {
            KeyCount keyCount = cache.get(ipKey);
            return keyCount.incr();
        } catch (ExecutionException e) {
            logger.error("获取ip访问次数异常：" + e.getMessage(), e);
            return -1;
        }
    }

    private String getIp(Object[] args) {
        HttpServletRequest request = null;
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
            }
        }
        try {
            if (request == null) {
                return null;
            }
            return HttpUtils.getIpAddress(request);
        } catch (Exception e) {
            logger.error("IpLimitAdapter getIpAddress error.", e);
            return null;
        }
    }
}
