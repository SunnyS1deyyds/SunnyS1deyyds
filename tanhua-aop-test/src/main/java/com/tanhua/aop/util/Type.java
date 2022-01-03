package com.tanhua.aop.util;

public enum Type {
    LOGIN("0101"),//登录
    REGISTER("0102");//注册

    private String code;

    Type(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}