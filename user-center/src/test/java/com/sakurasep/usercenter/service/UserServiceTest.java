package com.sakurasep.usercenter.service;

import com.sakurasep.usercenter.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.io.PrintStream;

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
        user.setIsDelete(0);


        boolean result = userService.save(user); // 返回的 boolean 类型
        System.out.println(user.getId());
        assertTrue(result);
    }


    // 密码测试
    @Test
    void userRegisterTest1() {
        String userAccount = "test123";
        String password = "123456";
        String checkpassword = "12345678";
        String planetCode = "1";
        String username = "测试用户";
        long result = userService.userRegister(userAccount, username, password, checkpassword, planetCode, "111");
    }

    @Test
    void userRegister() {
        String userAccount = "sakurasep111";
        String password = "12345678";
        String checkpassword = "12345678";
        String planetCode = "1";
        String username = "测试用户";
        long result = userService.userRegister(userAccount, username, password, checkpassword, planetCode, "111");
        System.out.println(result);
    }
}