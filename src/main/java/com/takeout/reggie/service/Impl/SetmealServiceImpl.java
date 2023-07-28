package com.takeout.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.DTO.DishDto;
import com.takeout.reggie.DTO.SetmealDto;
import com.takeout.reggie.mapper.SetmealMapper;
import com.takeout.reggie.pojo.Dish;
import com.takeout.reggie.pojo.DishFlavor;
import com.takeout.reggie.pojo.Setmeal;
import com.takeout.reggie.pojo.SetmealDish;
import com.takeout.reggie.service.SetmealDishService;
import com.takeout.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    @Transactional
    public void addSetmeal(SetmealDto setmealDto) {
        // check exit? update: save
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getId,setmealDto.getId());
        Setmeal setmeal = this.getOne(queryWrapper);
        if(setmeal!=null){
            this.updateById(setmealDto);
            // clear prev data in setmeal dish
            LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(SetmealDish::getSetmealId,setmealDto.getId());
            setmealDishService.remove(queryWrapper1);
        }
        else
            this.save(setmealDto);
        //update new data
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list = list.stream().map(
                (item)->{
                    item.setSetmealId(setmealDto.getId());
                    return item;
                }
        ).collect(Collectors.toList());
        setmealDishService.saveBatch(list);
    }

    @Override
    @Transactional
    public void deleteById(Long ids) {
        //删setmeal 表格中的数据
        this.removeById(ids);
        //删除setmealDish表格中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper);
    }

    @Override
    public List<SetmealDto> getByCategoryId(long categoryId) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,categoryId);
        List<Setmeal> list = this.list(queryWrapper);
        List<SetmealDto> setmealDtos = list.stream().map(
                (item)->{
                    SetmealDto setmealDto = new SetmealDto();
                    BeanUtils.copyProperties(item,setmealDto);

                    LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(SetmealDish::getSetmealId,setmealDto.getId());
                    setmealDto.setSetmealDishes(setmealDishService.list(queryWrapper1));
                    return setmealDto;
                }
        ).collect(Collectors.toList());
        return setmealDtos;
    }
}
