package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
