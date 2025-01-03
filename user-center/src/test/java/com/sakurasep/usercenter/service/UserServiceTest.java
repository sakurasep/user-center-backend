package com.sakurasep.usercenter.service;


import com.sakurasep.usercenter.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sakurasep
 * * @date 2024/10/12
 * 用户服务测试
 **/
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;
    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("sakurasep_admin");
        user.setUserAccount("123");
        user.setAvatar("https://static.sakurasep.top/assets/new_avatar_256.png");
        user.setGender(0);
        user.setPassword("123");
        user.setPhone("123");
        user.setEmail("123");


        boolean result = userService.save(user); // 返回的 boolean 类型
        System.out.println(user.getId());
        assertTrue(result);
    }


    @Test
    void userRegister() {
        // 初始值
        String userAccount = "sakurasep";
        String password = "";
        String checkpassword = "12345678";
        long result = userService.userRegister(userAccount, password, checkpassword);
        Assert.assertEquals(-1, result);

        // 用户账户小于四位
        userAccount = "sa";
        password = "12345678";
        result = userService.userRegister(userAccount, password, checkpassword);
        Assert.assertEquals(-1, result);

        // 密码小于八位
        userAccount = "sakurasep";
        password = "1234";
        result = userService.userRegister(userAccount, password, checkpassword);
        Assert.assertEquals(-1, result);

        // 用户名不能含有特殊字符
        userAccount = "sakura sep";
        password = "12345678";
        result = userService.userRegister(userAccount, password, checkpassword);
        Assert.assertEquals(-1, result);

        // 账户名不可以重复
        userAccount = "sakurasep";
        result = userService.userRegister(userAccount, password, checkpassword);
        Assert.assertEquals(-1, result);

        // 确认密码要与原密码相同
        password = "12345678";
        checkpassword = "123456";
        result = userService.userRegister(userAccount, password, checkpassword);
        Assert.assertEquals(-1, result);

        // 最终测试
        userAccount = "sakurasepadmin";
        checkpassword = "12345678";
        result = userService.userRegister(userAccount, password, checkpassword);
        Assert.assertTrue(result > 0);

    }
}