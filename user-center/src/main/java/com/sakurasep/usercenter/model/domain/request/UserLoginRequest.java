package com.sakurasep.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 * @author sakurasep
 * * @date 2025/1/3
 **/

@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 7614135190979606051L;

    private String userAccount;

    private String userPassword;

}
