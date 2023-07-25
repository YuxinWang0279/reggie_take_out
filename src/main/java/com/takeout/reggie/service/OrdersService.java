package com.takeout.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.reggie.pojo.Orders;

public interface OrdersService extends IService<Orders> {
    public void submit(Orders orders);
}
