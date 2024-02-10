package com.sky.controller.user;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSetmealController")
@Slf4j
@Api(tags = "C端-套餐管理相关接口")
@RequestMapping("/user/setmeal")
@EnableCaching
public class SetmealController {


    @Autowired
    private SetmealService setmealService;


    @ApiOperation("根据分类id查询套餐")
    @GetMapping("/list")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId")
    public Result<List<Setmeal>> selectByCategoryId(Long categoryId) {
        log.info("根据分类id查询套餐{}", categoryId);
        List<Setmeal> setmeals = setmealService.selectByCategoryId(categoryId);
        return Result.success(setmeals);
    }

    @ApiOperation("根据套餐id查询包含的菜品")
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> selectDishesBySetmealId(@PathVariable Long id){
        log.info("根据套餐id查询包含的菜品{}",id);
        List<DishItemVO> dishItemVOS = setmealService.selectDishesBySetmealId(id);
        return Result.success(dishItemVOS);
    }
}
