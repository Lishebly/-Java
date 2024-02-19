package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.MyList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageSelect(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据 id 查询订单
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);


    /**
     * 获取各种状态的数量
     * @return
     */
    List<MyList> getStatus();

    /**
     * 查询出当天营业额
     * @param map
     * @return
     */
    Double turnoverStatisticsByMap(Map map);

    /**
     *搜索订单
     * @param start
     * @param over
     * @param status
     * @return
     */
    Integer orderCount(LocalDateTime start, LocalDateTime over, Integer status);

    /**
     * 获取当天订单列表
     * @param start
     * @param over
     * @param status
     * @return
     */
    List<GoodsSalesDTO> getOrderListByLocalDateTimeAndStatus(LocalDateTime start, LocalDateTime over, Integer status);


}
