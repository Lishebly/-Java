package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = "工作台接口")
@RequestMapping("/admin/workspace")
public class WorkspaceController {
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 查询今日运营数据
     */
    @ApiOperation("查询今日运营数据")
    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData() {
        log.info("查询今日运营数据");
        return Result.success(workspaceService.businessData());
    }

    /**
     * 查询订单管理数据
     */
    @ApiOperation("查询订单管理数据")
    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> overviewOrders() {
        log.info("查询订单管理数据");
        return Result.success(workspaceService.overviewOrders());
    }

    /**
     * 查询菜品总览
     */
    @ApiOperation("查询菜品总览")
    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> overviewDishes() {
        log.info("查询菜品总览");
        return Result.success(workspaceService.overviewDishes());
    }

    /**
     * 查询套餐总览
     */
    @ApiOperation("查询套餐总览")
    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> overviewSetmeals() {
        log.info("查询套餐总览");
        return Result.success(workspaceService.overviewSetmeals());
    }
}
