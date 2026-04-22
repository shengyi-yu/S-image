package cn.com.laning.shengimage.service;

import cn.com.laning.shengimage.model.entity.User;
import cn.com.laning.shengimage.model.vo.LoginUserVO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

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
     * 用户注销
     * @param request
     * @return
     */
    Boolean userLogout(HttpServletRequest request);
}
