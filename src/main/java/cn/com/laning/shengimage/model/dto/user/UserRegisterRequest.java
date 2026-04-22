package cn.com.laning.shengimage.model.dto.user;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */

@Data
public class UserRegisterRequest implements Serializable {
    // 默认序列化版本号
    private static final long serialVersionUID = 1029239109393426845L;

    // 账号
    private String userAccount;

    // 密码
    private String userPassword;

    // 校验密码
    private String checkPassword;


}
