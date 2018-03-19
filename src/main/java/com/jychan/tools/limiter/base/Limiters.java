package com.jychan.tools.limiter.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置多个限制器
 *
 * Created by chenjinying on 2017/7/27.
 * mail: 415683089@qq.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Limiters {

    Limiter[] limiters();

}
