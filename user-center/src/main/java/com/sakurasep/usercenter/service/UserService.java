package com.sakurasep.usercenter.service;

import com.sakurasep.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author sakurasep
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-10-12 21:46:58
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 二次确认密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);
}
