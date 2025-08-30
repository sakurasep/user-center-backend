package com.sakurasep.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sakurasep.usercenter.common.BaseResponse;
import com.sakurasep.usercenter.common.ErrorCode;
import com.sakurasep.usercenter.common.ResultUtils;
import com.sakurasep.usercenter.exception.BusinessException;
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

import static com.sakurasep.usercenter.constant.UserConstant.USER_AVATAR;
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
    public long userRegister(String userAccount, String username, String userPassword, String checkPassword, String planetCode, String userAvatar) {
        // 1. 校验逻辑

        // todo 修改为自定义异常

        // 非空
        if (StringUtils.isAnyBlank(userAccount, username, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能为空");
        }

        // 长度
        if(planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号长度不能超过 5");
        }

        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于 4");
        }

        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于 8");
        }

        // 账户 & 昵称不能包含特殊字符 ^[a-zA-Z0-9_]+$
        String validPattern = "^[a-zA-Z0-9_]+$";
        Matcher accountMatcher = Pattern.compile(validPattern).matcher(userAccount);
        Matcher usernameMatcher = Pattern.compile(validPattern).matcher(username);
        if (!accountMatcher.matches() && usernameMatcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能包含特殊字符");  // 如果账户名 & 昵称包含特殊字符，返回 -1
        }


        // 账户不能够重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.NOT_REPEAT, "账户不能重复");
        }

        // 星球编号不能够重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planet_code", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if(count > 0) {
            throw new BusinessException(ErrorCode.NOT_REPEAT, "编号不能重复");
        }


        //密码与校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "重复密码不相同");
        }

        // 2. 加密密码
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPassword(newPassword);
        user.setIsDelete(0);
        user.setPlanetCode(planetCode);
        user.setUsername(username);
        user.setAvatar(USER_AVATAR);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OP_ERROR, "插入数据失败");
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验逻辑

        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
//            throw new BusinessException(ErrorCode.NULL_ERROR, "登录数据为空");
            return null;
        }

        // 长度
        if (userAccount.length() < 4) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于 4");
            return null;
        }

        if (userPassword.length() < 8) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于 8");
            return null;
        }

        // 账户不能包含特殊字符 ^[a-zA-Z0-9_]+$
        String validPattern = "^[a-zA-Z0-9_]+$";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (!matcher.matches()) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名包含特殊字符");
            return null;
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
        if (originUser == null) {
            return null;
        }
        // 用户脱敏
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatar(originUser.getAvatar());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        return safetyUser;
    }

    // 用户注销
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




