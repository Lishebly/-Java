package com.sky.controller.admin;

import com.sky.annotation.RedisFill;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {


    @Autowired
    private DishService dishService;

    @RedisFill
    @ApiOperation("新增菜品")
    @PostMapping
    public Result addDish(@RequestBody DishDTO dto){
        log.info("新增菜品{}",dto);
        dishService.addDish(dto);
        return Result.success();
    }



    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> dishesQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询{}",dishPageQueryDTO);
        PageResult result = dishService.dishesQuery(dishPageQueryDTO);
        return Result.success(result);
    }

    @RedisFill
    @ApiOperation("批量删除菜品")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品{}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @RedisFill
    @ApiOperation("菜品起售、停售")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable String status,Long id){
        log.info("菜品起售、停售");
        dishService.startOrStop(status,id);
        return Result.success();
    }

    @RedisFill
    @ApiOperation("修改菜品")
    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO){
        log.info("修改菜品{}",dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }


    @ApiOperation("根据id查询菜品")
    @GetMapping("/{id}")
    public Result getDishById(@PathVariable Long id){
        log.info("根据id查询菜品{}",id);
        DishVO dish = dishService.getDishById(id);
        return Result.success(dish);
    }

    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result listByCategoryId(Long categoryId){
        log.info("根据分类id查询菜品{}",categoryId);
        List<Dish> dishes = dishService.listByCategoryId(categoryId);
        return Result.success(dishes);
    }
}
