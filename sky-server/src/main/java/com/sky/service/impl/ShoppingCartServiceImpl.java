package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前是否有此项购物车数据
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        if (shoppingCarts == null || shoppingCarts.isEmpty()){
            //没有查到
            Long setmealId = shoppingCart.getSetmealId();
            Long dishId = shoppingCart.getDishId();
            if (setmealId != null){
                SetmealVO setmealVO = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setImage(setmealVO.getImage());
                shoppingCart.setAmount(setmealVO.getPrice());
            }else {
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartMapper.insert(shoppingCart);
            return;
        }
        //查到了加一
        ShoppingCart shoppingCart1 = shoppingCarts.get(0);
        shoppingCart1.setCreateTime(LocalDateTime.now());
        shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
        shoppingCartMapper.updateNumber(shoppingCart1);


    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> getShoppingCart() {
        Long id = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.getByUserId(id);
        return shoppingCarts;
    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    @Override
    public void deleteShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //查出对应的数据数量,减一,变成 0 则删除
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        ShoppingCart shoppingCart1 = list.get(0);
        Integer number = shoppingCart1.getNumber();
        if (number == 1){
            shoppingCartMapper.deleteSingle(shoppingCart1);
            return;
        }
        shoppingCart1.setNumber(number-1);
        shoppingCartMapper.updateNumber(shoppingCart1);

    }

    /**
     * 清空购物车
     */
    @Override
    public void deleteAll() {
        Long id = BaseContext.getCurrentId();
        shoppingCartMapper.deleteAll(id);
    }
}
