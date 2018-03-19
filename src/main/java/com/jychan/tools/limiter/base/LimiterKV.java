package com.jychan.tools.limiter.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by chenjinying on 2017/7/27.
 * mail: 415683089@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LimiterKV {

    String key();                           // 关键字
    String valString() default "";          // 值，字符串
    int valInt() default 0;                 // 值，int类型
    double valDouble() default 0;           // 值，double类型
    Class valClass() default String.class;  // 值，类型
    LimiterKvType type();                   // 值的类型

}
