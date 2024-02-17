package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    /**
     * 新增地址
     * @param addressBook
     */
    void insert(AddressBook addressBook);

    /**
     * 查询当前登录用户的所有地址信息
     * @param userId
     * @return
     */
    List<AddressBook> listByUserId(Long userId);

    /**
     * 获取默认地址信息
     * @param usrId
     * @return
     */
    AddressBook getDefaultAddressBook(Long usrId);

    /**
     * 修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    AddressBook getById(Long id);
}
