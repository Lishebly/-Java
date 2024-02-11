package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {




    /**
     * 修改购物车数量
     * @param shoppingCart
     */
    void updateNumber(ShoppingCart shoppingCart);

    /**
     * 新增购物车
     * @param shoppingCart
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户 id 以及 对应描述 及 套餐或 菜品 ID 查找对应购物车数据
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据用户 id 查询数据
     * @param id
     * @return
     */
    List<ShoppingCart> getByUserId(Long id);

    /**
     * 删除单条数据
     * @param shoppingCart1
     */
    void deleteSingle(ShoppingCart shoppingCart1);

    /**
     * 删除指定用户的所有购物车数据
     * @param id
     */
    void deleteAll(Long id);
}
