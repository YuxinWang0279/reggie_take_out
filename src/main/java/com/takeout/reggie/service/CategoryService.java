package com.takeout.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.takeout.reggie.pojo.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);

}
