package cn.com.laning.shengimage.service;

import cn.com.laning.shengimage.model.dto.user.UserQueryRequest;
import cn.com.laning.shengimage.model.entity.User;
import cn.com.laning.shengimage.model.vo.LoginUserVO;
import cn.com.laning.shengimage.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author lenovo
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2026-04-21 17:27:47
*/
public interface UserService extends IService<User> {

    /**
     * 注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 登录
     * @param userAccount
     * @param userPassword
     * @return
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取加密密码
     *
     * @param userPassword
     * @return
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取脱敏的登录用户信息
     *
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);


    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);


    /**
     * 获取脱敏的用户信息列标
     *
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 用户注销
     * @param request
     * @return
     */
    Boolean userLogout(HttpServletRequest request);


    /**
     * 转换QueryWrapper
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 判断用户是否为管理员
     *
     * @param user
     */
    boolean isAdmin(User user);

    /**
     * 修改个人信息
     *
     * @param loginUser 当前登录用户
     * @param userName 用户昵称
     * @param userAvatar 用户头像
     * @param userProfile 用户简介
     */
    void updateMyProfile(User loginUser, String userName, String userAvatar, String userProfile);

}
