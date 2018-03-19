package com.jychan.tools.limiter.base;

/**
 * 限制器返回结果
 *
 * Created by chenjinying on 2017/7/26.
 * mail: 415683089@qq.com
 */
public class LimiterResult {
    private boolean isPass; //是否通过
    private Object returnObj;//接口信息，根据接口业务返回，例如json

    public LimiterResult() {}

    public LimiterResult(boolean isPass, Object returnObj) {
        this.isPass = isPass;
        this.returnObj = returnObj;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LimiterResult2{");
        sb.append("isPass=").append(isPass);
        sb.append(", returnObj=").append(returnObj);
        sb.append('}');
        return sb.toString();
    }

    public boolean isPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    public Object getReturnObj() {
        return returnObj;
    }

    public void setReturnObj(Object returnObj) {
        this.returnObj = returnObj;
    }
}
