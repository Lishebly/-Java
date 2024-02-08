package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品
     * @param dto
     */
    @Override
    @Transactional
    public void addDish(DishDTO dto) {
        //插入菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto,dish);
        dishMapper.insert(dish);
        //获取生成主键值
        Long dishId = dish.getId();
        //插入口味
        List<DishFlavor> flavors = dto.getFlavors();
        if (flavors == null || flavors.isEmpty()) return;
        flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
        dishFlavorMapper.insertBatch(flavors);

    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult dishesQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.select(dishPageQueryDTO);
        List<DishVO> result = page.getResult();

        long total = page.getTotal();
        return new PageResult(total,result);
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //起售中代码不可删除
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断和套餐有无关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (!setmealIds.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除口味
        dishFlavorMapper.deleteById(ids);
        //删除自己
        dishMapper.deleteByIds(ids);
    }

    /**
     * 菜品起售、停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(String status, Long id) {
        if (Integer.parseInt(status) == StatusConstant.DISABLE){
            //判断起售的套餐里是否包含这个菜品,如果包含则不可以停售这个菜品
            List<Long> list = dishMapper.getSetmealById(id,StatusConstant.ENABLE);
            if (!list.isEmpty()){
                throw new BaseException(MessageConstant.SETMEAL_DISH_ON_SALE);
            }
        }
        Dish dish = Dish.builder().status(Integer.valueOf(status)).id(id).build();
        dishMapper.update(dish);
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateDish(DishDTO dishDTO) {
        //修改菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors.isEmpty()) return;

        //删除原来口味
        List<Long> list = List.of(dish.getId());
        dishFlavorMapper.deleteById(list);
        //加入新的口味

        flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
        dishFlavorMapper.insertBatch(flavors);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getDishById(Long id) {
        DishVO dishVO = dishMapper.selectById(id);
        List<DishFlavor> list = dishFlavorMapper.selectById(id);
        dishVO.setFlavors(list);
        return dishVO;
    }

    @Override
    public List<Dish> listByCategoryId(Long categoryId) {
        return dishMapper.listByCategoryId(categoryId);
    }
}
