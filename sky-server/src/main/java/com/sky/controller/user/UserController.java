package com.sky.controller.user;

import com.sky.dto.UserLoginDTO;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api("C端-用户接口")
@Slf4j
@RequestMapping("/user/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("用户登录{}",userLoginDTO);
        UserLoginVO userLoginVO = userService.login(userLoginDTO);
        return Result.success(userLoginVO);
    }
}
