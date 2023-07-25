package com.takeout.reggie.DTO;

import com.takeout.reggie.pojo.Dish;
import com.takeout.reggie.pojo.Setmeal;
import com.takeout.reggie.pojo.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;
    private String categoryName;
}
