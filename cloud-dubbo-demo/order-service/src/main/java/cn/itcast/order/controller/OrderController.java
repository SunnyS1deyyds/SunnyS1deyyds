package cn.itcast.order.controller;

import cn.itcast.dubbo.pojo.Order;
import cn.itcast.dubbo.pojo.User;
import cn.itcast.order.service.OrderService;
import cn.itcast.user.api.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @DubboReference
    private UserService userService;

    @GetMapping("{orderId}")
    public Order queryOrderByUserId(@PathVariable("orderId") Long orderId) {
        //根据订单id查询订单
        Order order = orderService.queryOrderById(orderId);

        //根据订单的用户ID查询用户
        User user = userService.queryById(order.getUserId());

        //把查询到的用户设置到订单中
        order.setUser(user);

        return order;
    }
}
