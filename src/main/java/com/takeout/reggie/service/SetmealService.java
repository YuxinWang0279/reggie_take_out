package com.takeout.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.reggie.DTO.SetmealDto;
import com.takeout.reggie.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void addSetmeal(SetmealDto setmealDto);

    public void deleteById(Long ids);

    List<SetmealDto> getByCategoryId(long categoryId);
}
