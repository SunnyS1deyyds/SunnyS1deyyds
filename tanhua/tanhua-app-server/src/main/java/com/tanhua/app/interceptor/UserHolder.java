package com.tanhua.app.interceptor;

import com.itheima.model.pojo.User;

public class UserHolder {

    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    //设置用户
    public static void setUser(User user) {
        userThreadLocal.set(user);
    }

    //获取用户
    public static User getUser() {
        return userThreadLocal.get();
    }

    //移除用户
    public static void removeUser() {
        userThreadLocal.remove();
    }

    //获取用户主键id
    public static Long getUserId() {
        return userThreadLocal.get().getId();
    }

    //获取用户的电话号码
    public static String getUserMobile() {
        return userThreadLocal.get().getMobile();
    }
}
