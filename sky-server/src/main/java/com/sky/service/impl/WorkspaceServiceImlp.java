package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkspaceServiceImlp implements WorkspaceService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 查询今日运营数据
     *
     * @return
     */
    @Override
    public BusinessDataVO businessData() {
        //查询今日营业额
        Map map = new HashMap<>();
        LocalDate localDate = LocalDate.now();
        map.put("begin", localDate.atStartOfDay());
        map.put("end", LocalDateTime.of(localDate, LocalTime.MAX));
        map.put("status", Orders.COMPLETED);
        Double turnover = orderMapper.turnoverStatisticsByMap(map);
        //查询今日新增用户
        Integer newUserCount = userMapper.getNewUserCount(map);
        //查询今日有效订单数
        Integer validOrderCount = orderMapper.orderCount((LocalDateTime) map.get("begin"), (LocalDateTime) map.get("end"), Orders.COMPLETED);
        //查询今日订单完成率
        Integer totalOrderCount = orderMapper.orderCount((LocalDateTime) map.get("begin"), (LocalDateTime) map.get("end"), null);
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount.doubleValue() / totalOrderCount;
        //查询今日平均客单价
        Double averageOrderAmount = validOrderCount == 0 ? 0.0 : turnover / validOrderCount;
        return BusinessDataVO.builder()
                .turnover(turnover == null ? 0.0 : turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(averageOrderAmount)
                .newUsers(newUserCount).build();

    }

    /**
     * 查询订单管理数据
     *
     * @return
     */
    @Override
    public OrderOverViewVO overviewOrders() {
        //查询今日订单数量
        List<MyList> mapperStatus = orderMapper.getStatus();
        OrderOverViewVO orderOverViewVO = new OrderOverViewVO();
        Integer total = 0;
        for (MyList myList : mapperStatus) {
            Integer status = myList.getStatus();
            Integer count = myList.getCount();
            total += count;
            if (status.equals(Orders.TO_BE_CONFIRMED)) {
                orderOverViewVO.setWaitingOrders(count);
            } else if (status.equals(Orders.CONFIRMED)) {
                orderOverViewVO.setDeliveredOrders(count);
            } else if (status.equals(Orders.COMPLETED)) {
                orderOverViewVO.setCompletedOrders(count);
            } else if (status.equals(Orders.CANCELLED)) {
                orderOverViewVO.setCancelledOrders(count);
            }
        }
        orderOverViewVO.setAllOrders(total);
        return orderOverViewVO;
    }

    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public DishOverViewVO overviewDishes() {
        //查询菜品停售起售数量
        //查询菜品停售总数
        Integer discontinuedCount = dishMapper.countByStatus(StatusConstant.DISABLE);
        //查询菜品起售总数
        Integer soldCount = dishMapper.countByStatus(StatusConstant.ENABLE);
        return DishOverViewVO.builder()
                .sold(soldCount)
                .discontinued(discontinuedCount)
                .build();
    }

    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO overviewSetmeals() {
        //查询套餐停售起售数量
        //查询套餐停售总数
        Integer discontinuedCount = setmealMapper.countByStatus(StatusConstant.DISABLE);
        //查询套餐起售总数
        Integer soldCount = setmealMapper.countByStatus(StatusConstant.ENABLE);
        return SetmealOverViewVO.builder()
                .sold(soldCount)
                .discontinued(discontinuedCount)
                .build();
    }
}
