package com.takeout.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.takeout.reggie.common.BaseContext;
import com.takeout.reggie.common.R;
import com.takeout.reggie.pojo.ShoppingCart;
import com.takeout.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @GetMapping("/list")
    public R<List<ShoppingCart>> cartList(){
        Long userId =BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> ans = shoppingCartService.list(queryWrapper);
        return R.success(ans);

    }

    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(HttpServletRequest request, @RequestBody ShoppingCart shoppingCart){
        //设置当前点单的用户id信息
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart result = shoppingCartService.addToCart(shoppingCart);
        return R.success(result);
    }

    @DeleteMapping("/clean")
    public R<String> delete(){
        Long userId =BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(queryWrapper);
        return R.success("删除成功");
    }
}
