package com.sakurasep.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sakurasep.usercenter.model.domain.User;
import com.sakurasep.usercenter.model.domain.request.UserLoginRequest;
import com.sakurasep.usercenter.model.domain.request.UserRegisterRequest;
import com.sakurasep.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sakurasep.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.sakurasep.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author sakurasep
 * * @date 2025/1/3
 **/

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        // 基础校验，不通过则不进入业务逻辑层
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }

        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    @PostMapping("login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        // 基础校验，不通过则不进入业务逻辑层
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }

        return userService.userLogin(userAccount, userPassword, request);
    }

    /**
     * 根据用户名查询
     *
     * @return 返回查询到的用户信息
     */
    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        if (isNotAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        return userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
    }

    /**
     * 根据前端传回的 id 删除用户
     *
     * @param id 用户 id
     * @return 返回删除的状态
     */
    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (isNotAdmin(request)) {
            return false;
        }
        if (id <= 0) {
            return false;
        }
        return userService.removeById(id);

    }

    /**
     * 鉴权
     * @param request 请求体
     * @return true 非管理员 false 管理员
     */
    private boolean isNotAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        // 鉴权
        return user == null || user.getUserRole() != ADMIN_ROLE;
    }

}
