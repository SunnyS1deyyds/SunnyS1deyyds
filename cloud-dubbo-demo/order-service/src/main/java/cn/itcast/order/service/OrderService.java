package cn.itcast.order.service;


import cn.itcast.dubbo.pojo.Order;

public interface OrderService {

    Order queryOrderById(Long orderId);
}
