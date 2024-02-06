package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {



    void update(Category category);


    Page<Category> select(CategoryPageQueryDTO categoryPageQueryDTO);

    void insert(Category category);

    @Delete("delete from category where id = #{id}")
    void delete(Long id);

    List<Category> showByType(String type);
}
