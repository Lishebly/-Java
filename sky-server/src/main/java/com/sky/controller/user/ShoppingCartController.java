package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@EnableCaching
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端-购物车接口")
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;

    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车{}",shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation("查看购物车")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> getShoppingCart(){
        log.info("查看购物车");
        return Result.success(shoppingCartService.getShoppingCart());
    }


    @ApiOperation("删除购物车中一个商品")
    @PostMapping("/sub")
    public Result deleteShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车中一个商品{}",shoppingCartDTO);
        shoppingCartService.deleteShoppingCart(shoppingCartDTO);
        return Result.success();
    }


    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public Result deleteAll(){
        log.info("清空购物车");
        shoppingCartService.deleteAll();
        return Result.success();
    }
}
