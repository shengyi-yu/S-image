package cn.com.laning.shengimage.model.dto.user;


import lombok.Data;

import java.io.Serializable;

/**
 * 登录
 */

@Data
public class UserLoginRequest implements Serializable {
    // 默认序列化版本号
    private static final long serialVersionUID = 1029239109393426845L;

    // 账号
    private String userAccount;

    // 密码
    private String userPassword;


}
