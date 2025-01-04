package com.sakurasep.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sakurasep.usercenter.model.domain.User;
import com.sakurasep.usercenter.service.UserService;
import com.sakurasep.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sakurasep.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author sakurasep
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-10-12 21:46:58
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    // 盐值混淆密码
    private static final String SALT = "sakura_password";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验逻辑

        // todo 修改为自定义异常

        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return -1;
        }

        // 长度
        if (userAccount.length() < 4) {
            return -1;
        }

        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }

        // 账户不能包含特殊字符 ^[a-zA-Z0-9_]+$
        String validPattern = "^[a-zA-Z0-9_]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            return -1;  // 如果账户名包含特殊字符，返回 -1
        }


        // 账户不能够重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1;
        }

        //密码与校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }

        // 2. 加密密码
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPassword(newPassword);
        user.setIsDelete(0);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验逻辑

        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }

        // 长度
        if (userAccount.length() < 4) {
            return null;
        }

        if (userPassword.length() < 8) {
            return null;
        }

        // 账户不能包含特殊字符 ^[a-zA-Z0-9_]+$
        String validPattern = "^[a-zA-Z0-9_]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
            return null;  // 如果账户名包含特殊字符，返回 -1
        }


        // 2. 加密密码
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("password", newPassword);
        User user = userMapper.selectOne(queryWrapper);

        // 用户不存在时的处理逻辑
        if (user == null) {
            log.info("user login failed, can not find userAccount or can not match userPassword");
            return null;
        }

        User safetyUser = getSafetyUser(user);

        // 用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        // 返回脱敏后的用户信息
        return safetyUser;


    }

    @Override
    public User getSafetyUser(User originUser) {
        // 用户脱敏
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatar(originUser.getAvatar());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        return safetyUser;
    }
}




