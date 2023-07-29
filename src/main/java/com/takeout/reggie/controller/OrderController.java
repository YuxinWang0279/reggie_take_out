package com.takeout.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.reggie.DTO.DishDto;
import com.takeout.reggie.DTO.OrderDto;
import com.takeout.reggie.common.BaseContext;
import com.takeout.reggie.common.R;
import com.takeout.reggie.pojo.Dish;
import com.takeout.reggie.pojo.OrderDetail;
import com.takeout.reggie.pojo.Orders;
import com.takeout.reggie.pojo.ShoppingCart;
import com.takeout.reggie.service.OrderDetailService;
import com.takeout.reggie.service.OrdersService;
import com.takeout.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);

        return R.success("提交成功");
    }

    @PostMapping("/again")
    public R<String> orderAgain(@RequestBody Orders orders){
        long id = orders.getId();
        //1. get order dish/ setmeal
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper);
        //2. add dish / setmeal to shopping cart
        orderDetails.stream().forEach(
                (item)->{
                    //construct shoppingcart entity
                    ShoppingCart shoppingCart = new ShoppingCart();

                    shoppingCart.setName(item.getName());
                    shoppingCart.setUserId(BaseContext.getCurrentId());
                    shoppingCart.setDishId(item.getDishId());
                    shoppingCart.setSetmealId(item.getSetmealId());
                    shoppingCart.setDishFlavor(item.getDishFlavor());
                    shoppingCart.setNumber(item.getNumber());
                    shoppingCart.setAmount(item.getAmount());
                    shoppingCart.setImage(item.getImage());
                    //add to shopping cart
                    shoppingCartService.addToCart(shoppingCart);
                }
        );
        return R.success("Order again successfully!");
    }
    @GetMapping("page")
    public R<Page> page(int page, int pageSize, String number, LocalDateTime beginTime,LocalDateTime endTime){
        log.info("page:{},pageSize:{},number:{}, begin TIME:{}, end Time:{}",page,pageSize,number,beginTime,endTime);
        //分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.like(number!=null,Orders::getNumber,number);
        queryWrapper.between(beginTime!=null && endTime!=null,Orders::getOrderTime,beginTime,endTime);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        ordersService.page(pageInfo,queryWrapper);


        return R.success(pageInfo);
    }

    /**
     * edit orders
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> editStatus(@RequestBody Orders orders){
        Orders order = ordersService.getById(orders.getId());
        order.setStatus(orders.getStatus());
        ordersService.updateById(order);
        return R.success("Edit order status successfully");
    }

    @GetMapping("userPage")
    public R<Page> userOrderInfo(int page, int pageSize){
        long userId = BaseContext.getCurrentId();
        //分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> pageInfo_final = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.eq(Orders::getUserId,userId);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        ordersService.page(pageInfo,queryWrapper);

        List<Orders> orders = pageInfo.getRecords();
        List<OrderDto> orderDtos = orders.stream().map(
                (item)->{
                    OrderDto orderDto = new OrderDto();
                    BeanUtils.copyProperties(item,orderDto);
                    long orderId = item.getId();
                    LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(OrderDetail::getOrderId,orderId);
                    orderDto.setOrderDetailList(orderDetailService.list(queryWrapper1));
                    return orderDto;
                }
        ).collect(Collectors.toList());

        pageInfo_final.setRecords(orderDtos);
        return R.success(pageInfo_final);
    }
}
