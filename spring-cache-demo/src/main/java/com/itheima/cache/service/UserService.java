package com.itheima.cache.service;

import com.itheima.cache.dao.UserDao;
import com.itheima.cache.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

//@Cacheable 有缓存使用，没有缓存执行方法，并把执行结果放到缓存中
//@CachePut :把方法执行的结果放到缓存中
//@CacheEvict：删除缓存
//@Caching:可以同时操作多个缓存
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    //value是缓存空间，名字任意，不同操作，缓存空间不一样
    //key:缓存的key，可以使用SpringEL表达式
    //@Cacheable(value = "user", key = "'uid'+#id")
    //@CachePut(value = "user", key = "'uid'+#id")
    @Caching(cacheable = {
            @Cacheable(value = "user", key = "#id"),
            @Cacheable(value = "user", key = "'uid'+#id")
    })
    public User findById(Integer id) {
        return userDao.findById(id);
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#id"),
            @CacheEvict(value = "user", key = "'uid'+#id")
    })
    //@CacheEvict(value = "user", key = "'uid'+#id")
    public void update(Integer id) {
        userDao.update(id);
    }
}
