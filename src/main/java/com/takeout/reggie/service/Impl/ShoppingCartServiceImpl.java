package com.takeout.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.mapper.ShoppingCartMapper;
import com.takeout.reggie.pojo.ShoppingCart;
import com.takeout.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Controller;

@Controller
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public ShoppingCart addToCart(ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        //添加菜品
        if(shoppingCart.getDishId()!=null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }
        //添加套餐
        else{
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart current = this.getOne(queryWrapper);
        //增加数量
        if(current!=null){
            current.setNumber(current.getNumber()+1);
            this.updateById(current);
        }
        //添加新
        else{
            shoppingCart.setNumber(1);
            this.save(shoppingCart);
            current = shoppingCart;
        }
        return current;
    }
}
