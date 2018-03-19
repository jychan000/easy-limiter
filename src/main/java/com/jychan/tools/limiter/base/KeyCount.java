package com.jychan.tools.limiter.base;

/**
 * Created by chenjinying on 2017/8/3.
 * mail: 415683089@qq.com
 */
public class KeyCount {

    private String key;
    private int count;
    private String mark;

    public KeyCount() {
    }

    public KeyCount(String key, int count) {
        this.key = key;
        this.count = count;
    }

    public int incr() {
        return ++count;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KeyCount{");
        sb.append("key='").append(key).append('\'');
        sb.append(", count=").append(count);
        sb.append(", mark='").append(mark).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getKey() {return key;}
    public void setKey(String key) {this.key = key;}
    public int getCount() {return count;}
    public void setCount(int count) {this.count = count;}
    public String getMark() {return mark;}
    public void setMark(String mark) {this.mark = mark;}
}
