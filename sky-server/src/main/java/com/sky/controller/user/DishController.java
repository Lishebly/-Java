package com.sky.controller.user;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品相关接口")
public class DishController {


    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result listByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品{}",categoryId);
        String key = "dish_"+categoryId;
        //获取缓存中的数据
        List<DishVO> dishVOS = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //如果有直接返回
        if (dishVOS != null && !dishVOS.isEmpty()){
            return Result.success(dishVOS);
        }
        //没有则查询再存到缓存
        List<DishVO> dishes = dishService.list1ByCategoryId(categoryId);
        redisTemplate.opsForValue().set(key,dishes);
        return Result.success(dishes);
    }
}
