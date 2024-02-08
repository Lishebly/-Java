package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    /**
     * 批量新增套餐菜品关系
     * @param setmealDishes
     */
    void insert(List<SetmealDish> setmealDishes);

    /**
     * 批量删除关系根据套餐 id
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 查看套餐内是否含有未起售的菜品,有的话无法起售
     * @param status
     * @param id
     * @return
     */
    List<Long> selectNotOnSealDishesInSetmeal(Integer status, Long id);

    /**
     * 根据套餐 id 查询出所有联系
     * @param id
     * @return
     */
    List<SetmealDish> getBySetmealId(Long id);
}
