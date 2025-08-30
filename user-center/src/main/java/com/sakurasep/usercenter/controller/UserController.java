package com.sakurasep.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sakurasep.usercenter.common.BaseResponse;
import com.sakurasep.usercenter.common.ErrorCode;
import com.sakurasep.usercenter.common.ResultUtils;
import com.sakurasep.usercenter.exception.BusinessException;
import com.sakurasep.usercenter.model.domain.User;
import com.sakurasep.usercenter.model.domain.request.UserLoginRequest;
import com.sakurasep.usercenter.model.domain.request.UserRegisterRequest;
import com.sakurasep.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sakurasep.usercenter.constant.UserConstant.*;

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
    public BaseResponse<Long>userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String plantCode = userRegisterRequest.getPlanetCode();
        String username = userRegisterRequest.getUsername();

        // 基础校验，不通过则不进入业务逻辑层
        if (StringUtils.isAnyBlank(userAccount, username, userPassword, checkPassword, plantCode)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        long result = userService.userRegister(userAccount, username, userPassword, checkPassword, plantCode, USER_AVATAR);

        return ResultUtils.success(result);
    }

    @PostMapping("login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.failure(ErrorCode.NULL_ERROR);
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        // 基础校验，不通过则不进入业务逻辑层
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.failure(ErrorCode.NULL_ERROR);
        }

        User user = userService.userLogin(userAccount, userPassword, request);

        if(user == null) {
            return ResultUtils.failure(ErrorCode.LOGIN_ERROR);
        }
        else {
            return ResultUtils.success(user);
        }
    }


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        int result = userService.userLogout(request);

        return ResultUtils.success(result);
    }

    @GetMapping("current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long userId = currentUser.getId();
        // todo 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);


    }


    /**
     * 根据用户名查询
     *
     * @return 返回查询到的用户信息
     */
    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        if (isNotAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return list;
    }

    /**
     * 根据前端传回的 id 删除用户
     *
     * @param id 用户 id
     * @return 返回删除的状态
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (isNotAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean b = userService.removeById(id);

        return ResultUtils.success(b);

    }

    /**
     * 鉴权
     *
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
