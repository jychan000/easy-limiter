## easy-limiter

### 功能介绍

为WEB项目快速支持限流功能，包括接口总请求限流、ip限流。也可以自定义限流规则。

前提：项目须支持spring注解！

### 引用依赖
```
<dependency>
    <groupId>com.jychan.tools</groupId>
    <artifactId>easy-limiter</artifactId>
    <version>1.0.5</version>
</dependency>
```
---- 引入spring aop相关依赖 ----

spring-boot 可用：
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```


### 扫描包
注解方式扫包:  
`@ComponentScan({"com.jychan.tools.limiter"})`

xml方式扫包：  
`<context:component-scan base-package="com.jychan.tools.limiter" />`


### 配置限流(快速使用)  
我们提供两个快速限流注解  
总量限流：`@LimiterQuickTotal`  
针对ip限流：`@LimiterQuickIp`（注意，需要方法有`HttpServletRequest`类型参数才生效）  
```
@LimiterQuickIp(tps = 5, rejectReturnVal = "{\"msg\":\"ip too often!\",\"code\":\"1506\"}")
@LimiterQuickTotal(tps = 3, rejectReturnVal = "{\"msg\":\"system busy.\",\"code\":\"1506\"}")
public Map<String, Object> testLimit(HttpServletRequest request) {
    Map<String, Object> map = new HashMap<>();
    map.put("code", "0");
    return map;
}
```
属性`tps`表示限流器的tps上限  
属性`rejectReturnVal`是当限流器拦截后返回的内容，会根据被限流方法的返回类型自动转换，具体来说是基本类型由字符串直转，其他类型以json->obj方式转换

### 配置限流(高级使用)
```
@Limiters(limiters = {
    @Limiter(adapter = LimiterRandomAdapter.class,
        rejectReturnVal = "{\"msg\":\"system busy.\",\"code\":\"1506\"}",
        keyValues = {@LimiterKV(key = "rate", valDouble = 0.4, type = LimiterKvType.DOUBLE)}),
    @Limiter(adapter = LimiterIpAdapter.class,
        rejectReturnVal = "{\"msg\":\"ip too often.\",\"code\":\"1506\"}",
        keyValues = {
            @LimiterKV(key = KEY_RETURN_VAL, valString = "{\"msg\":\"ip too often.....\",\"code\":\"1506\"}", type = LimiterKvType.STRING),
            @LimiterKV(key = KEY_TPS, valInt = 3, type = LimiterKvType.INT)})
})
public Map<String, Object> testLimit(HttpServletRequest request, HttpServletResponse response) {
    Map<String, Object> map = new HashMap<>();
    map.put("code", 0);
    return map;
}
```
说明：  
用户自定义限流器并实现`com.jychan.tools.limiter.base.BaseLimiter`接口，创建bean，如：LimiterRandomAdapter  
拦截工具会根据`LimiterRandomAdapter.class`提取限流器bean  
根据需要通过`keyValues`传递需要的参数进自定义的限流器  
温馨提示：建议自定义限流器时参考`LimiterIpAdapter、LimiterTotalAdapter、LimiterRandomAdapter`的实现


### 发布版本（for开发者）
执行`$ mvn deploy`

