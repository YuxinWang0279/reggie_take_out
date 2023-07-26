package com.takeout.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.reggie.DTO.DishDto;
import com.takeout.reggie.pojo.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void add(DishDto dishDto);

    public DishDto getByIdWithFlavors(Long id);

    public void updateWithFlavors(DishDto dishDto);

    List<DishDto> getByCategoryId(long categoryId, Integer status);
}
