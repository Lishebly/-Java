package com.sky.service.impl;

import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.*;
import com.sky.websocket.WebSocketServer;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;
    @Value("${sky.shop.address}")
    private String shopAddress;
    @Value("${sky.baidu.ak}")
    private String ak;
    /**
     * 用户下单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //抛出异常情况(地址不为空,购物车不为空)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> byUserId = shoppingCartMapper.getByUserId(userId);
        if (byUserId == null || byUserId.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //TODO 有了百度账号以后填入配置,再解除注释测试
//        checkOutOfRange(addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail());
        //插入订单数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPayStatus(Orders.UN_PAID);
        orderMapper.insert(orders);
        //插入订单详情数据
        List<OrderDetail> list = new ArrayList<>();
        for (ShoppingCart shoppingCart : byUserId) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            list.add(orderDetail);
        }
        orderDetailMapper.insertBatch(list);
        OrderSubmitVO submitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return submitVO;
    }


    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        Map map = new HashMap<>();
        map.put("type",1);//1表示来单提醒 2 表示客户催单
        map.put("orderId",ordersDB.getId());
        map.put("content","订单号"+outTradeNo);
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    /**
     * 历史订单查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult getHistoryOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Page<Orders> page = orderMapper.pageSelect(ordersPageQueryDTO);
        List<Orders> result = page.getResult();
        List<OrderVO> list = new ArrayList<>();
        result.forEach(orders -> {
            Long ordersId = orders.getId();
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(ordersId);
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            orderVO.setOrderDetailList(orderDetailList);
            list.add(orderVO);
        });
        long total = page.getTotal();
        return new PageResult(total, list);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetails(Long id) {
        OrderVO orderVO = new OrderVO();
        Orders orders = orderMapper.getById(id);
        List<OrderDetail> list = orderDetailMapper.getByOrderId(id);
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(list);
        return orderVO;
    }

    /**
     * 取消订单
     *
     * @param id
     */
    @Override
    public void cancelOrder(Long id) throws Exception {
        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        Orders orderMapperById = orderMapper.getById(id);
        if (orderMapperById == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (orderMapperById.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders order = Orders.builder().id(id).status(Orders.CANCELLED)
                .cancelTime(LocalDateTime.now())
                .cancelReason("用户取消").build();
        if (orderMapperById.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //TODO 退款
            System.out.println("退款");
            //调用微信支付退款接口
            weChatPayUtil.refund(
                    orderMapperById.getNumber(), //商户订单号
                    orderMapperById.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
            order.setPayStatus(Orders.REFUND);
        }
        orderMapper.update(order);
    }

    /**
     * 更新购物车
     * @param id
     */
    @Override
    @Transactional
    public void placeAnotherOrder(Long id) {
        Long userId = BaseContext.getCurrentId();

        shoppingCartMapper.deleteAll(userId);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        List<ShoppingCart> cartList = orderDetails.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(userId);
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartMapper.insertBatch(cartList);
    }

    /**
     * 订单搜索
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageSelect(ordersPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 获取状态
     * @return
     */
    @Override
    public OrderStatisticsVO getStatus() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        List<MyList> status = orderMapper.getStatus();
        Map<Integer, Integer> map = new HashMap<>();
        for (MyList myList : status) {
            map.put(myList.getStatus(), myList.getCount());
        }
        orderStatisticsVO.setConfirmed(map.get(Orders.CONFIRMED));
        orderStatisticsVO.setToBeConfirmed(map.get(Orders.TO_BE_CONFIRMED));
        orderStatisticsVO.setDeliveryInProgress(map.get(Orders.DELIVERY_IN_PROGRESS));
        return orderStatisticsVO;
    }

    /**
     * 接单
     *
     * @param
     */
    @Override
    @Transactional
    public void takeOrders(OrdersConfirmDTO ordersConfirmDTO) {
        //查询订单判断是否为空
        Orders orders1 = orderMapper.getById(ordersConfirmDTO.getId());
        if (orders1 == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //修改状态
        Orders orders = Orders.builder().id(ordersConfirmDTO.getId()).status(Orders.CONFIRMED).build();
        orderMapper.update(orders);
    }

    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
        //查询订单判断是否为空
        Orders orders1 = orderMapper.getById(ordersRejectionDTO.getId());
        if (orders1 == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!orders1.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //TODO 退款
        //支付状态
        Integer payStatus = orders1.getPayStatus();
        if (payStatus == Orders.PAID) {
            //用户已支付，需要退款
            /*String refund = null;
            try {
                refund = weChatPayUtil.refund(
                        orders1.getNumber(),
                        orders1.getNumber(),
                        new BigDecimal(0.01),
                        new BigDecimal(0.01));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.info("申请退款：{}", refund);*/
            log.info("退款");
        }
        //修改状态
        Orders orders = Orders.builder()
                .id(ordersRejectionDTO.getId())
                .status(Orders.CANCELLED)
                .cancelReason(ordersRejectionDTO.getRejectionReason())
                .cancelTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }

    /**
     * 商家取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOrder1(OrdersCancelDTO ordersCancelDTO) {

        Orders orders = orderMapper.getById(ordersCancelDTO.getId());
        Integer payStatus = orders.getPayStatus();
        if (payStatus == 1){
            //需要退款,用户已经支付
            String refund = null;
           /* try {
                refund = weChatPayUtil.refund(
                        orders.getNumber(),
                        orders.getNumber(),
                        new BigDecimal(0.01),
                        new BigDecimal(0.01));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            log.info("申请退款：{}", refund);*/
            log.info("退款");
        }
        orders = Orders.builder()
                .id(ordersCancelDTO.getId())
                .cancelTime(LocalDateTime.now())
                .cancelReason(ordersCancelDTO.getCancelReason())
                .status(Orders.CANCELLED)
                .build();
        orderMapper.update(orders);
    }

    /**
     * 修改订单状态为派送中
     * @param id
     */
    @Override
    public void delivery(Long id) {
        //查询订单判断是否为空
        Orders orders1 = orderMapper.getById(id);
        if (orders1 == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!orders1.getStatus().equals(Orders.CONFIRMED)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        orderMapper.update(orders);
    }

    /**
     * 修改订单为完成
     * @param id
     */
    @Override
    public void complete(Long id) {
        //查询订单判断是否为空
        Orders orders1 = orderMapper.getById(id);
        if (orders1 == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!orders1.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = Orders.builder()
                .status(Orders.COMPLETED)
                .id(id)
                .deliveryTime(LocalDateTime.now())
                .build();
        orderMapper.update(orders);
    }

    /**
     * 催单
     * @param id
     */
    @Override
    public void reminder(Long id) {
        Map map = new HashMap<>();
        map.put("type",2);//1表示来单提醒 2 表示客户催单
        Orders orders = orderMapper.getById(id);
        map.put("orderId",orders.getId());
        map.put("content","订单号"+orders.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    /**
     * 检查客户的收货地址是否超出配送范围
     * @param address
     */
    private void checkOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address",shopAddress);
        map.put("output","json");
        map.put("ak",ak);

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 5000){
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }
    }
}
