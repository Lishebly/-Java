package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "管理端订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @ApiOperation("订单搜索")
    @GetMapping("/conditionSearch")
    public Result<PageResult> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜索{}",ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     */
    @ApiOperation("各个状态的订单数量统计")
    @GetMapping("/statistics")
    public Result<OrderStatisticsVO> getStatus(){
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.getStatus();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     */
    @ApiOperation("查询订单详情")
    @GetMapping("/details/{id}")
    public Result<OrderVO> getOrderDetails(@PathVariable Long id){
        log.info("查询订单详情{}",id);
        OrderVO orderVO = orderService.getOrderDetails(id);
        return Result.success(orderVO);
    }
    /**
     * 接单
     */
    @ApiOperation("接单")
    @PutMapping("/confirm")
    public Result takeOrders(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("接单{}",ordersConfirmDTO);
        orderService.takeOrders(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     */
    @ApiOperation("拒单")
    @PutMapping("/rejection")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒单{}",ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }
    /**
     * 取消订单
     */
    @ApiOperation("取消订单")
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单{}",ordersCancelDTO);
        orderService.cancelOrder1(ordersCancelDTO);
        return Result.success();
    }
    /**
     * 派送订单
     */
    @ApiOperation("派送订单")
    @PutMapping("/delivery/{id}")
    public Result delivery(@PathVariable Long id){
        log.info("派送订单{}",id);
        orderService.delivery(id);
        return Result.success();
    }
    /**
     * 完成订单
     */
    @ApiOperation("完成订单")
    @PutMapping("/complete/{id}")
    public Result complete(@PathVariable Long id){
        log.info("完成订单{}",id);
        orderService.complete(id);
        return Result.success();
    }
}
