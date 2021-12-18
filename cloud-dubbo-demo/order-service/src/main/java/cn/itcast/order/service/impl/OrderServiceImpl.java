package cn.itcast.order.service.impl;

import cn.itcast.dubbo.pojo.Order;
import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public Order queryOrderById(Long orderId) {
        // 1.查询订单
        return orderMapper.findById(orderId);
    }
}
