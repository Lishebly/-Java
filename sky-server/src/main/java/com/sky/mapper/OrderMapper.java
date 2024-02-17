package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 更新订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据状态和时间查询订单
     * @param pendingPayment
     * @param time
     * @return
     */
    @Select("select * from orders where status = #{pendingPayment} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer pendingPayment, LocalDateTime time);
}
