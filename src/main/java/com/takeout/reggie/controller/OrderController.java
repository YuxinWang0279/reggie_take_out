package com.takeout.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.reggie.DTO.DishDto;
import com.takeout.reggie.common.R;
import com.takeout.reggie.pojo.Dish;
import com.takeout.reggie.pojo.Orders;
import com.takeout.reggie.service.OrdersService;
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
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);

        return R.success("提交成功");
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
}
