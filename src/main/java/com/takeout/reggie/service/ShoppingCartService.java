package com.takeout.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.reggie.pojo.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    ShoppingCart addToCart(ShoppingCart shoppingCart);
}
