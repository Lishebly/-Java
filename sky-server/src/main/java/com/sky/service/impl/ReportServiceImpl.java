package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    /**
     * 营业额统计接口
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        //获取日期,放入集合
        List<LocalDate> localDateList = new ArrayList<>();
        getDateList(begin, end, localDateList);
        //获取销量放入集合
        List<Double> sales = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            //数据库中查询出,begin,end,status,返回结果为 double
            Map map = new HashMap<>();
            map.put("begin",LocalDateTime.of(localDate, LocalTime.MIN));
            map.put("end",LocalDateTime.of(localDate, LocalTime.MAX));
            map.put("status", Orders.COMPLETED);
            Double sale = orderMapper.turnoverStatisticsByMap(map);
            sale = sale == null ? 0.0 :sale;
            sales.add(sale);
        }
        //调用工具类拼接
        //返回
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(localDateList,","))
                .turnoverList(StringUtils.join(sales,","))
                .build();
    }

    private static void getDateList(LocalDate begin, LocalDate end, List<LocalDate> localDateList) {
        while (!begin.equals(end.plusDays(1))) {
            localDateList.add(begin);
            begin = begin.plusDays(1);
        }
    }

    /**
     * 用户统计接口
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //获取日期
        List<LocalDate> localDateList = new ArrayList<>();
        getDateList(begin,end,localDateList);
        //遍历日期列表,得到每一天的用户总量以及新增用户
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            Map map = new HashMap<>();
            map.put("begin",LocalDateTime.of(localDate,LocalTime.MIN));
            map.put("end",LocalDateTime.of(localDate,LocalTime.MAX));
            Integer totalUser = userMapper.getAllUserCount(map);
            Integer newUser = userMapper.getNewUserCount(map);
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(localDateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 订单统计接口
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //获取日期列表
        List<LocalDate> localDateList = new ArrayList<>();
        getDateList(begin,end,localDateList);
        //遍历日期列表获取每日订单数
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate localDate : localDateList) {
            LocalDateTime start = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime over = LocalDateTime.of(localDate, LocalTime.MAX);
            //获取每日订单总数
            Integer orderCount = orderMapper.orderCount(start,over,null);
            //获取每日有效订单数
            Integer validOrderCount = orderMapper.orderCount(start,over,Orders.COMPLETED);
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //获取时间区域内订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //获取时间区域内有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //获取订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0){
            orderCompletionRate = (double) validOrderCount / totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(localDateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 查询销量排名top10接口
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        //获取开始日期和结束日期的 localDateTime形式
        LocalDateTime start = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime over = LocalDateTime.of(end, LocalTime.MAX);
        //查询在这个时间段中状态为完成的订单
        Integer status = Orders.COMPLETED;
        List<GoodsSalesDTO> ordersList = orderMapper.getOrderListByLocalDateTimeAndStatus(start, over, status);

        //创建两个集合,一个存放商品名称,一个存放销量
        List<String> goodsNameList = ordersList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> goodsSalesList = ordersList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        //返回
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(goodsNameList,","))
                .numberList(StringUtils.join(goodsSalesList,","))
                .build();
    }
}
