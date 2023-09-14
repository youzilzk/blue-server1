package com.youzi.blue.server.controller;

import com.youzi.blue.server.entity.User;
import com.youzi.blue.server.pojo.Response;
import com.youzi.blue.server.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/user")
@Controller
@ResponseBody
public class UserController {

    @Autowired
    IUserService userService;

    @GetMapping("/device")
    public Response<List<Map>> device(String username) {
        List<Map> device = userService.device(username);
        return new Response<>(true, "success", device);
    }

    @GetMapping("/updateDevice")
    public Response<Boolean> addDevice(String username, String watchUser, String token) {
        int code = userService.updateDevice(username, watchUser, token);
        switch (code) {
            case 1:
                return new Response<>(false, "设备不存在!", null);
            case 2:
                return new Response<>(false, "验证码错误!", null);
            case 3:
                return new Response<>(true, "添加成功!", null);
            case 4:
                return new Response<>(false, "该设备已添加!", null);
            case 5:
                return new Response<>(true, "更新成功!", null);
            default:
                return new Response<>(false, "未知错误!", null);
        }

    }

    @GetMapping("/deleteWatchUser")
    public Response<Boolean> deleteWatchUser(String username, String watchUser) {
        userService.deleteWatchUser(username, watchUser);
        return new Response<>(true, "删除成功!", null);

    }

    @GetMapping("/getToken")
    public Response<String> getToken(String username) {
        String token = userService.getToken(username);
        return new Response<>(true, "success!", token);
    }

    @GetMapping("/updateToken")
    public Response<String> updateToken(String username, @RequestParam(required = false) String token) {
        String watchToken = userService.updateToken(username, token);
        return new Response<>(true, "success!", watchToken);
    }

    @GetMapping("/auth")
    public Response<Boolean> auth(String username, String password) {
        User user = userService.auth(username, password);
        if (user == null) {
            return new Response<>(false, "用户名不存在!", null);
        }
        if (user.getPassword().equals(password)) {
            return new Response<>(true, "认证成功!", null);
        } else {
            return new Response<>(false, "密码错误!", null);
        }
    }

    @PostMapping("/register")
    public Response<User> register(User user) {
        User register = userService.register(user);
        if (register == null) {
            return new Response<>(false, "用户名已存在!", null);
        } else {
            return new Response<>(true, "注册成功!", register);
        }
    }
}
