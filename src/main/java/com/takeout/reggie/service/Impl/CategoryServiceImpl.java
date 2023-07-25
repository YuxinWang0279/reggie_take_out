package com.takeout.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.common.CustomException;
import com.takeout.reggie.mapper.CategoryMapper;
import com.takeout.reggie.pojo.Category;
import com.takeout.reggie.pojo.Dish;
import com.takeout.reggie.pojo.Setmeal;
import com.takeout.reggie.service.CategoryService;
import com.takeout.reggie.service.DishService;
import com.takeout.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //判断当前Id是否关联菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(queryWrapper);
        if(count>0){
            throw new CustomException("该分类下关联有菜品，无法删除");
        }

        //判断当前Id是否关联套餐
        LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(queryWrapper2);
        if(count2>0){
            throw new CustomException("该分类下关联有套餐，无法删除");
        }
        super.removeById(id);

    }


}
