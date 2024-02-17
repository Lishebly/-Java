package com.sky.task;

import com.alibaba.fastjson.JSON;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class WebSocketTask {
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 通过WebSocket每隔5秒向客户端发送消息
     */
//    @Scheduled(cron = "0/5 * * * * ?")
    public void sendMessageToClient() {
        log.info("起送");
        Map map = new HashMap<>();
        map.put("type",1);//1表示来单提醒 2 表示客户催单
        map.put("orderId","111111");
        map.put("content","订单号"+"11111");
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }
}
