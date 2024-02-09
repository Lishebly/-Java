package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    void deleteById(List<Long> ids);

    /**
     * 根据菜品 id 查询对应口味
     * @param id
     * @return
     */
    List<DishFlavor> selectById(Long id);
}
