package com.takeout.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.reggie.DTO.DishDto;
import com.takeout.reggie.common.R;
import com.takeout.reggie.pojo.Category;
import com.takeout.reggie.pojo.Dish;
import com.takeout.reggie.pojo.DishFlavor;
import com.takeout.reggie.pojo.Employee;
import com.takeout.reggie.service.CategoryService;
import com.takeout.reggie.service.DishFlavorService;
import com.takeout.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@Slf4j
public class DishController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 根据type查询分类
     * @param category
     * @return
     */
    @GetMapping("/category/list")
    public R<List<Category>> getCategoryByType(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> ans = categoryService.list(queryWrapper);
        return R.success(ans);
    }

    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping("/dish")
    public R<String> updateByID(@RequestBody DishDto dishDto){
        dishService.add(dishDto);
        //delete relevant redis cache data
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("添加成功");
    }

    @PutMapping("/dish")
    public R<String> editByID(@RequestBody DishDto dishDto){
        dishService.updateWithFlavors(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("添加成功");
    }
    @GetMapping("/dish/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);
        //分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> pageInfo_Final=new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo,queryWrapper);

        //将查询数据除了records进行拷贝
        BeanUtils.copyProperties(pageInfo,pageInfo_Final,"records");

        //除了pageInfo 中categories项
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> records_final = null;

        records_final = records.stream().map(
                (item)->{
                    //获取categoryName，并返回DishDto
                    DishDto dishdto = new DishDto();
                    Long categoryId = item.getCategoryId();
                    String categoryName = categoryService.getById(categoryId).getName();
                    BeanUtils.copyProperties(item,dishdto);
                    dishdto.setCategoryName(categoryName);
                    return dishdto;
                }
        ).collect(Collectors.toList());

        pageInfo_Final.setRecords(records_final);
        return R.success(pageInfo_Final);
    }

    /**
     * 数据回显
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<DishDto> queryById(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavors(id);
        return R.success(dishDto);

    }

    @GetMapping("/dish/list")
    public R<List<DishDto>> getDishByCategoryId(Long categoryId,Integer status){
        String key = "dish_" + categoryId;
        if(status!=null){
            key+= "_" + status;
        }
        //construct key
        List<DishDto> list = null;
        //search in redis
        list = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //if found, return
        if(list!=null) return R.success(list);
        //if not, query from database, and store the result to redis
        list = dishService.getByCategoryId(categoryId,status);
        redisTemplate.opsForValue().set(key,list,60, TimeUnit.MINUTES);
        return R.success(list);
    }
}
