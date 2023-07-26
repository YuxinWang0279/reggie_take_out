package com.takeout.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.reggie.DTO.DishDto;
import com.takeout.reggie.DTO.SetmealDto;
import com.takeout.reggie.common.R;
import com.takeout.reggie.pojo.Employee;
import com.takeout.reggie.pojo.Setmeal;
import com.takeout.reggie.service.CategoryService;
import com.takeout.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @CacheEvict(value = "setmealCache",allEntries = true)
    @PostMapping
    public R<String> addSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.addSetmeal(setmealDto);
        return R.success("更新成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> pageInfo_Dto = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo,queryWrapper);

        //更新categoryName
        BeanUtils.copyProperties(pageInfo,pageInfo_Dto,"records");

        List<Setmeal> setmealList = pageInfo.getRecords();
        pageInfo_Dto.setRecords(setmealList.stream().map(
                (item)->{
                    SetmealDto setmealDto = new SetmealDto();
                    Long id = item.getCategoryId();
                    String categoryName = categoryService.getById(id).getName();
                    BeanUtils.copyProperties(item,setmealDto);
                    setmealDto.setCategoryName(categoryName);
                    return setmealDto;
                }
        ).collect(Collectors.toList()));
        return R.success(pageInfo_Dto);
    }


    @CacheEvict(value = "setmealCache",allEntries = true)
    @DeleteMapping
    public R<String> deleteById(Long ids){
        setmealService.deleteById(ids);
        return R.success("删除成功");
    }

    @Cacheable(value = "setmealCache",key = "#categoryId")
    @GetMapping("list")
    public R<List<SetmealDto>> getSetmealByCategoryId(long categoryId){
        List<SetmealDto> list = setmealService.getByCategoryId(categoryId);
        return R.success(list);
    }
}
