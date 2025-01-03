package com.sakurasep.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author sakurasep
 * * @date 2024/10/12
 **/

@SpringBootTest
public class UserCenterApplicationTests {

    @Test
    void testDigest() {
        String newPassword = DigestUtils.md5DigestAsHex(("abcd" + "mypassword").getBytes());
        System.out.println(newPassword);
    }
}
