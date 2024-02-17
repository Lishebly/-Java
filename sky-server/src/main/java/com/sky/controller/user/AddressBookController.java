package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端-地址簿接口")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @ApiOperation("新增地址")
    @PostMapping
    public Result addAddressBook(@RequestBody AddressBook addressBook){
        log.info("新增地址{}",addressBook);
        addressBookService.addAddressBook(addressBook);
        return Result.success();
    }


    @ApiOperation("查询当前登录用户的所有地址信息")
    @GetMapping("/list")
    public Result<List<AddressBook>> listAll(){
        log.info("查询当前登录用户的所有地址信息");
        return Result.success(addressBookService.listAll());
    }

    @ApiOperation("查询默认地址")
    @GetMapping("/default")
    public Result<AddressBook> getDefaultAddressBook(){
        log.info("查询默认地址");
        return Result.success(addressBookService.getDefaultAddressBook());
    }

    @ApiOperation("根据id修改地址")
    @PutMapping
    public Result updateAddressBook(@RequestBody AddressBook addressBook){
        log.info("根据id修改地址");
        addressBookService.updateAddressBook(addressBook);
        return Result.success();
    }

    @ApiOperation("根据id删除地址")
    @DeleteMapping
    public Result deleteAddressBook(Long id){
        log.info("根据id删除地址{}",id);
        addressBookService.deleteById(id);
        return Result.success();
    }

    @ApiOperation("根据id查询地址")
    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id){
        log.info("根据id查询地址{}",id);
        return Result.success(addressBookService.getById(id));

    }


    @ApiOperation("设置默认地址")
    @PutMapping("/default")
    public Result setDefaultById(@RequestBody AddressBook addressBook){
        log.info("设置默认地址");
        addressBookService.setDefaultById(addressBook);
        return Result.success();
    }

}
