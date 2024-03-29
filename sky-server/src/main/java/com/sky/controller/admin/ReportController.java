package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@Slf4j
@Api(tags = "数据统计接口")
@RequestMapping("/admin/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计接口
     */
    @ApiOperation("营业额统计接口")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("营业额统计接口{}{}", begin, end);
        return Result.success(reportService.turnoverStatistics(begin, end));
    }

    /**
     * 用户统计接口
     */
    @ApiOperation("用户统计接口")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计接口{}{}", begin, end);
        return Result.success(reportService.userStatistics(begin,end));
    }
    /**
     * 订单统计接口
     */
    @ApiOperation("订单统计接口")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ){
        log.info("订单统计接口{}{}", begin, end);
        return Result.success(reportService.ordersStatistics(begin,end));
    }

    /**
     * 查询销量排名top10接口
     */
    @ApiOperation("查询销量排名top10接口")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ){
        log.info("查询销量排名top10接口{}{}", begin, end);
        return Result.success(reportService.top10(begin,end));
    }
}
