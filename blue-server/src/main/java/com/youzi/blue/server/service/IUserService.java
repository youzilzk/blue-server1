package com.youzi.blue.server.service;

import com.youzi.blue.server.entity.User;

import java.util.List;
import java.util.Map;

public interface IUserService {
    /**
     * 认证
     */
    User auth(String username, String password);

    /**
     * 注册
     */
    User register(User user);

    /**
     * 添加设备列表
     */
    Integer updateDevice(String username, String watchUser, String token);

    /**
     * 删除设备列表项
     */
    void deleteWatchUser(String username, String watchUser);

    /**
     * 获取用户token
     */
    String getToken(String username);

    /**
     * 更新token
     */
    String updateToken(String username, String token);

    /**
     * 用户列表
     */
    List<Map> device(String userName);

    /**
     * 更新用户
     */
    void updateUser(User user);

    /**
     * 更新用户状态
     */
    void updateUserState(String userId, Integer state);

    /**
     * 获取权限
     */
    Boolean isPermit(String username, String watchUser);
}
