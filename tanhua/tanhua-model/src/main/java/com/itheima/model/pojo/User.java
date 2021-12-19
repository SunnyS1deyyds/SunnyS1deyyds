package com.itheima.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor  //满参构造方法
@NoArgsConstructor   //无参构造方法
public class User extends BasePojo {

    private Long id;
    private String mobile;
    private String password;

    private String hxUser;  //环信用户名
    private String hxPassword;  //环信密码
}