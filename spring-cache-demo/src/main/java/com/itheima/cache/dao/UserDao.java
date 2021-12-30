package com.itheima.cache.dao;

import com.itheima.cache.pojo.User;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class UserDao {

    public User findById(Integer id){
        System.out.println("查询数据库");
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new User(id,"张三");
    }

    public void update(Integer id) {
        System.out.println("根据id更新");
    }
}
