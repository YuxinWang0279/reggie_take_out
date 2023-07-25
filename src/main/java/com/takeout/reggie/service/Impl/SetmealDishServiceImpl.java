package com.takeout.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.mapper.SetmealDishMapper;
import com.takeout.reggie.mapper.SetmealMapper;
import com.takeout.reggie.pojo.SetmealDish;
import com.takeout.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
