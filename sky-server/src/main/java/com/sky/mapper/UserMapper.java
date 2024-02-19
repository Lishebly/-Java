package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据 openid 查询用户
     * @param openid
     * @return
     */
    User selectByOpenid(String openid);

    /**
     * 创建新用户
     * @param user1
     */
    void insert(User user1);

    /**
     * 根据 id 查找用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 获取当天所有用户数量
     * @return
     */
    Integer getAllUserCount(Map map);

    /**
     * 获取当天新增用户数量
     * @param map
     * @return
     */
    Integer getNewUserCount(Map map);
}
