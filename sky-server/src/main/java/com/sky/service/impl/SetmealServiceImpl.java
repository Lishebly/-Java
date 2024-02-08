package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        //插入套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.insert(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //将插入的 id 加入到菜品列表里
        Long id = setmeal.getId();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(id));
        ////插入套餐与菜品联系
        setmealDishMapper.insert(setmealDishes);

    }

    /**
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult QueryByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.selectByPage(setmealPageQueryDTO);
        List<SetmealVO> result = page.getResult();
        long total = page.getTotal();
        return new PageResult(total,result);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    @Override
    public void deleteByIds(List<Long> ids) {
        //起售套餐不可删除
        List<Long> setmeals = setmealMapper.selectByStatusOnSeal(ids, StatusConstant.ENABLE);
        if (!setmeals.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        //删除自己
        setmealMapper.deleteByIds(ids);
        //删除套餐菜品对应关系
        setmealDishMapper.deleteByIds(ids);

    }

    /**
     * 套餐起售、停售
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        if (status == StatusConstant.ENABLE){
            //查看套餐内是否含有未起售的菜品,有的话无法起售
            List<Long> DishIds = setmealDishMapper.selectNotOnSealDishesInSetmeal(StatusConstant.DISABLE,id);
            if (!DishIds.isEmpty()){
                throw new BaseException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
            Setmeal setmeal = Setmeal.builder().status(status).id(id).build();
            setmealMapper.update(setmeal);
        } else if (status == StatusConstant.DISABLE) {
            Setmeal setmeal = Setmeal.builder().status(status).id(id).build();
            setmealMapper.update(setmeal);
        }
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        //套餐表中查询对应信息
        SetmealVO setmealVO = setmealMapper.getById(id);
        //套餐菜品关系表中查询对应信息
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes.isEmpty()) return;

        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealDTO.getId()));
        //删
        setmealDishMapper.deleteByIds(List.of(setmealDTO.getId()));
        //加
        setmealDishMapper.insert(setmealDishes);
    }
}
