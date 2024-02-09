package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);


    Page<DishVO> select(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    void deleteByIds(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    DishVO selectById(Long id);

    List<Dish> listByCategoryId(Long categoryId);

    /**
     * 判断起售的套餐里是否包含这个菜品,如果包含则不可以停售这个菜品
     * @param id
     * @return
     */
    List<Long> getSetmealById(Long id, Integer status);

    /**
     * 根据分类id查询菜品users
     * @param categoryId
     * @return
     */
    List<DishVO> listByCategoryIdOnClient(Long categoryId);
}
