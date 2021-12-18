package cn.itcast.user.controller;


import cn.itcast.user.pojo.User;
import cn.itcast.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    //@Autowired
    //@DubboReference(check = false)
    //@DubboReference(version = "2.0")
    //@DubboReference(retries = 1)
    //@DubboReference(timeout = 5000)
    @DubboReference
    private UserService userService;

    /**
     * 路径： /user/username/110
     *
     * @param id 用户id
     */
    @GetMapping("/username/{id}")
    public String queryUsernameById(@PathVariable("id") Long id) {
        return userService.queryUsernameById(id);
    }

    /**
     * 路径： /user/110
     *
     * @param id 用户id
     */
    @GetMapping("/{id}")
    public User queryById(@PathVariable("id") Long id) {
        return userService.queryById(id);
    }
}
