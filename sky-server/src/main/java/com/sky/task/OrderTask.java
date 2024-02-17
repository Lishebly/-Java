package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单(每分钟触发一次)
     */
    @Scheduled(cron = "0 * * * * ?")
//    @Scheduled(cron = "1/5 * * * * ?")
    public void processTimeoutOrder() {
        log.info("定时处理超时订单:{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().minusMinutes(15L);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, time);

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时,自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }

    }

    /**
     * 每天凌晨 1 点执行
     */
    @Scheduled(cron = "0 1 * * *")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void process() {
        log.info("定时处理派送中的订单{}", LocalDateTime.now());
        //查找状态为派送中的订单,把状态改为完成
//        LocalDateTime time = LocalDateTime.now().minusMinutes(60);
        LocalDateTime time = LocalDateTime.now();
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);

        if (ordersList != null && !ordersList.isEmpty()) {
            for (Orders orders : ordersList) {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }

}
