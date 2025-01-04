package com.sakurasep.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * @author sakurasep
 * * @date 2025/1/3
 **/

@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -1596374423040072551L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

}
