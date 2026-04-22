package cn.com.laning.shengimage.controller;


import cn.com.laning.shengimage.common.BaseResponse;
import cn.com.laning.shengimage.common.ResultUtils;
import cn.com.laning.shengimage.exception.ErrorCode;
import cn.com.laning.shengimage.exception.ThrowUtils;
import cn.com.laning.shengimage.model.dto.user.UserLoginRequest;
import cn.com.laning.shengimage.model.dto.user.UserRegisterRequest;
import cn.com.laning.shengimage.model.entity.User;
import cn.com.laning.shengimage.model.vo.LoginUserVO;
import cn.com.laning.shengimage.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 健康检查
 *
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }
    /**
     *  获取当前登录用户
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        Boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }
}
