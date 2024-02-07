package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {



    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);


    Page<Category> select(CategoryPageQueryDTO categoryPageQueryDTO);

    @AutoFill(OperationType.INSERT)
    void insert(Category category);

    @Delete("delete from category where id = #{id}")
    void delete(Long id);

    List<Category> showByType(String type);
}
