package com.takeout.reggie.DTO;

import com.takeout.reggie.pojo.OrderDetail;
import com.takeout.reggie.pojo.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDto extends Orders {
    List<OrderDetail> orderDetailList;
}
