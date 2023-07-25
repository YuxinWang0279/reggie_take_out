package com.takeout.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.DTO.DishDto;
import com.takeout.reggie.mapper.DishMapper;
import com.takeout.reggie.pojo.Dish;
import com.takeout.reggie.pojo.DishFlavor;
import com.takeout.reggie.service.DishFlavorService;
import com.takeout.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Transactional
    @Override
    public void add(DishDto dishDto) {
        this.save(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(
                (item)->{
                    item.setDishId(dishDto.getId());
                    return item;
                }
        ).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavors(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish,dishDto);

        List<DishFlavor> flavors = null;
        // search flavors by dish id
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    public void updateWithFlavors(DishDto dishDto) {
        //更新dish
        this.updateById(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        //删除dish flavors表格中，关于本dishid的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //新增口味信息
        flavors = flavors.stream().map(
                (item)->{
                    item.setDishId(dishDto.getId());
                    return item;
                }
        ).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public List<DishDto> getByCategoryId(long categoryId) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);
        List<Dish> list = this.list(queryWrapper);
        List<DishDto> dishDtos = list.stream().map(
                (item)->{
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(item,dishDto);

                    LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(DishFlavor::getDishId,dishDto.getId());
                    dishDto.setFlavors(dishFlavorService.list(queryWrapper1));
                    return dishDto;
                }
        ).collect(Collectors.toList());
        return dishDtos;
    }
}
