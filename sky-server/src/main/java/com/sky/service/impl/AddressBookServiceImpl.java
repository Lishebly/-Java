package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void addAddressBook(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBook.setIsDefault(StatusConstant.DISABLE);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    @Override
    public List<AddressBook> listAll() {
        Long userId = BaseContext.getCurrentId();
        return addressBookMapper.listByUserId(userId);
    }

    /**
     * 查询默认地址
     * @return
     */
    @Override
    public AddressBook getDefaultAddressBook() {
        Long usrId = BaseContext.getCurrentId();
        return addressBookMapper.getDefaultAddressBook(usrId);

    }

    /**
     * 根据id修改地址
     * @param addressBook
     */
    @Override
    public void updateAddressBook(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    /**
     * 设置默认地址
     * @param id
     */
    @Override
    @Transactional
    public void setDefaultById(AddressBook addressBook) {
        //把之前的默认取消
        Long usrId = BaseContext.getCurrentId();
        AddressBook defaultAddressBook = addressBookMapper.getDefaultAddressBook(usrId);
        if (defaultAddressBook != null){
            defaultAddressBook.setIsDefault(StatusConstant.DISABLE);
            addressBookMapper.update(defaultAddressBook);
        }
        //当前设置成新的默认
        addressBook.setIsDefault(StatusConstant.ENABLE);
        addressBookMapper.update(addressBook);
    }
}
