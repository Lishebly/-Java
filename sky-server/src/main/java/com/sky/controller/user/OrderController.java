package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value = "userOrderController")
@Slf4j
@Api(tags = "C端-订单接口")
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @ApiOperation("用户下单")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单{}", ordersSubmitDTO);
        return Result.success(orderService.submit(ordersSubmitDTO));

    }

    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("历史订单查询{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.getHistoryOrders(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getOrderDetails(@PathVariable Long id){
        log.info("查询订单详情{}",id);
        return Result.success(orderService.getOrderDetails(id));
    }

    /**
     * 取消订单
     */
    @ApiOperation("取消订单")
    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Long id) throws Exception {
        log.info("取消订单");
        orderService.cancelOrder(id);
        return Result.success();
    }
    /**
     * 再来一单
     */
    @ApiOperation("再来一单")
    @PostMapping("/repetition/{id}")
    public Result placeAnotherOrder(@PathVariable Long id){
        log.info("再来一单{}",id);
        orderService.placeAnotherOrder(id);
        return Result.success();
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }
}
