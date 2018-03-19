package com.jychan.tools.limiter.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 基础限流
 *
 * Created by chenjinying on 2018/3/5.
 * mail: 415683089@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LimiterQuickTotal {

    /**
     * 总TPS上限
     */
    int tps() default 0;

    /**
     * 当被拦截时返回的内容，会根据限流的方法定义的返回类型做转换，
     * 如果返回类型为 String：会直接返回 rejectReturnVal 内容
     * 如果返回类型为基本数据类型（boolean、byte、short、int、long、float、double、char）：会把 rejectReturnVal 识别成该类型
     * 如果返回类型为引用类型（Map、List、自定义类等）：会执行 json -> obj 得到实例返回
     */
    String rejectReturnVal() default "";

}
