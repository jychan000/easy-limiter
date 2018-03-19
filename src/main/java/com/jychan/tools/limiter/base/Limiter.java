package com.jychan.tools.limiter.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限制器，注解接口
 * 原则上除非限制器明确拦截，否则都放过
 * Created by chenjinying on 2017/7/26.
 * mail: 415683089@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Limiter {

    Class<? extends BaseLimiter> adapter();     //限制器适配器bean类

    LimiterKV[] keyValues() default {};         //限制器需要的参数

    /**
     * 当被拦截时返回的内容，会根据限流的方法定义的返回类型做转换，
     * 如果返回类型为 String：会直接返回 rejectReturnVal 内容
     * 如果返回类型为基本数据类型（boolean、byte、short、int、long、float、double、char）：会把 rejectReturnVal 识别成该类型
     * 如果返回类型为引用类型（Map、List、自定义类等）：会执行 json -> obj 得到实例返回
     */
    String rejectReturnVal() default "";
}
