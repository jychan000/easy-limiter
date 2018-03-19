package com.jychan.tools.limiter.base;

import com.jychan.tools.limiter.adapter.LimiterIpAdapter;
import com.jychan.tools.limiter.adapter.LimiterTotalAdapter;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by chenjinying on 2017/7/26.
 * mail: 415683089@qq.com
 */
@Aspect
@Component
public class LimiterAop {

    private static Logger logger = Logger.getLogger(LimiterAop.class);

    @Autowired
    private ApplicationContext appContext;

    @Pointcut("@annotation(com.jychan.tools.limiter.base.Limiters)")
    public void recordLimiters() {}

    @Pointcut("@annotation(com.jychan.tools.limiter.base.Limiter)")
    public void recordLimiter() {}

    @Pointcut("@annotation(com.jychan.tools.limiter.base.LimiterQuickTotal)")
    public void recordLimiterQuickTotal() {}

    @Pointcut("@annotation(com.jychan.tools.limiter.base.LimiterQuickIp)")
    public void recordLimiterQuickIp() {}


    @Around("recordLimiterQuickTotal()")
    public Object dealLimiterQuickTotal2(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            final MethodInvocationProceedingJoinPoint joinPoint = (MethodInvocationProceedingJoinPoint) proceedingJoinPoint;
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();
            String packageName = method.getDeclaringClass().getName();
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();

            LimiterQuickTotal limiterConf = method.getAnnotation(LimiterQuickTotal.class);
            int tps = limiterConf.tps();
            String rejectReturnVal = limiterConf.rejectReturnVal();

            LimiterKVObj[] kvs = new LimiterKVObj[1];
            kvs[0] = new LimiterKVObj(LimiterTotalAdapter.KEY_TPS, tps);

            LimiterResult result = limitInner(LimiterTotalAdapter.class, proceedingJoinPoint, packageName, methodName, rejectReturnVal, returnType, kvs, args);
            return returnResult(proceedingJoinPoint, args, result);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Around("recordLimiterQuickIp()")
    public Object dealLimiterQuickIp(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            final MethodInvocationProceedingJoinPoint joinPoint = (MethodInvocationProceedingJoinPoint) proceedingJoinPoint;
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();
            String packageName = method.getDeclaringClass().getName();
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();

            LimiterQuickIp limiterConf = method.getAnnotation(LimiterQuickIp.class);
            int tps = limiterConf.tps();
            String rejectReturnVal = limiterConf.rejectReturnVal();

            LimiterKVObj[] kvs = new LimiterKVObj[1];
            kvs[0] = new LimiterKVObj(LimiterIpAdapter.KEY_TPS, tps);

            LimiterResult result = limitInner(LimiterIpAdapter.class, proceedingJoinPoint, packageName, methodName, rejectReturnVal, returnType, kvs, args);
            return returnResult(proceedingJoinPoint, args, result);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Around("recordLimiters()")
    public Object dealAopLimiters(ProceedingJoinPoint proceedingJoinPoint) {
        final MethodInvocationProceedingJoinPoint joinPoint = (MethodInvocationProceedingJoinPoint) proceedingJoinPoint;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String packageName = method.getDeclaringClass().getName();
        String methodName = method.getName();
        Class<?> returnType = method.getReturnType();

        Limiters annotation = method.getAnnotation(Limiters.class);

        LimiterResult result = new LimiterResult(true, "pass");
        Limiter[] limiterConfs = annotation.limiters();
        for (Limiter limiterConf : limiterConfs) {
            result = limit(proceedingJoinPoint, limiterConf, packageName, methodName, returnType, args);
            if (result != null && !result.isPass()) {
                return returnResult(proceedingJoinPoint, args, result);
            }
        }
        return returnResult(proceedingJoinPoint, args, result);
    }

    @Around("recordLimiter()")
    public Object dealAopLimiter(ProceedingJoinPoint proceedingJoinPoint) {
        final MethodInvocationProceedingJoinPoint joinPoint = (MethodInvocationProceedingJoinPoint) proceedingJoinPoint;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String packageName = method.getDeclaringClass().getName();
        String methodName = method.getName();
        Class<?> returnType = method.getReturnType();

        Limiter limiterConf = method.getAnnotation(Limiter.class);

        LimiterResult result = limit(proceedingJoinPoint, limiterConf, packageName, methodName, returnType, args);
        return returnResult(proceedingJoinPoint, args, result);
    }

    private LimiterResult limit(ProceedingJoinPoint proceedingJoinPoint, Limiter limiterConf, String packageName, String method, Class<?> returnType, Object[] args) {
        try {
            Class<? extends BaseLimiter> adapterClass = limiterConf.adapter();
            LimiterKV[] keyValues = limiterConf.keyValues();
            String rejectReturnVal = limiterConf.rejectReturnVal();

            return limitInner(adapterClass, proceedingJoinPoint, packageName, method, rejectReturnVal, returnType, keyValues, args);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new LimiterResult(true, "处理异常放行，" + e.getMessage());
        }
    }

    public LimiterResult limitInner(Class<? extends BaseLimiter> adapterClass, ProceedingJoinPoint proceedingJoinPoint, String packageName, String methodName, String rejectReturnVal, Class<?> returnType, LimiterKV[] kvs, Object[] args) throws Exception {
        BaseLimiter limiter = appContext.getBean(adapterClass);
        if (limiter == null) {
            logger.error("获取bean失败，检查@Limiter配置");
            return new LimiterResult(true, "获取bean失败，检查@Limiter配置");
        }
        LimiterResult result = limiter.isPass(packageName, methodName, rejectReturnVal, returnType, kvs, args);
        return result;
    }

    /**
     * 返回结果
     */
    private Object returnResult(ProceedingJoinPoint proceedingJoinPoint, Object[] args, LimiterResult result) {
        if (result != null && !result.isPass()) {
            logger.error("请求被限流器拦截, " + result.getReturnObj());
            return result.getReturnObj();
        } else {
            try {
                return proceed(proceedingJoinPoint, args);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
    }

    /**
     * 继续执行业务逻辑
     */
    private Object proceed(ProceedingJoinPoint joinpoint, Object[] args) throws Exception {
        try {
            return joinpoint.proceed(args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
