package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private static final String url = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        //发送数据到微信服务端获取 openid
        String openid = getopenid(userLoginDTO);
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //根据 openid 判断是否在数据库里有此用户
        User user = userMapper.selectByOpenid(openid);
        if (user == null){
            //创建新用户
            User user1 = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            userMapper.insert(user1);
            user = user1;
        }
        //生成 token 令牌
        Map map = new HashMap<>();
        map.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), map);
        //返回数据
        return new UserLoginVO(user.getId(),openid,token);
    }

    private String getopenid(UserLoginDTO userLoginDTO) {
        String code = userLoginDTO.getCode();
        String appid = weChatProperties.getAppid();
        String secret = weChatProperties.getSecret();
        Map map = new HashMap<>();
        map.put("js_code",code);
        map.put("appid",appid);
        map.put("secret",secret);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(url, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
