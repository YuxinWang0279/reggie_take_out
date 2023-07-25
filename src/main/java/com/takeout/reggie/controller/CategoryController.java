package com.takeout.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.reggie.common.R;
import com.takeout.reggie.pojo.Category;
import com.takeout.reggie.pojo.Dish;
import com.takeout.reggie.pojo.Employee;
import com.takeout.reggie.service.CategoryService;
import com.takeout.reggie.service.DishService;
import com.takeout.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public R<String>  save(@RequestBody Category category){
        log.info("添加菜品分类:{}",category);
        categoryService.save(category);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        log.info("page:{},pageSize:{}",page,pageSize);
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /*
    删除分类
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("根据id删除分类,{}",ids);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");

    }

    @PutMapping
    public R<String> edit(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改分类成功");
    }
}
