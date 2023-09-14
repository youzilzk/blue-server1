package com.youzi.blue.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youzi.blue.common.utils.UuidUtil;
import com.youzi.blue.server.entity.Relation;
import com.youzi.blue.server.entity.User;
import com.youzi.blue.server.manager.ChannelManager;
import com.youzi.blue.server.mappers.CommonMapper;
import com.youzi.blue.server.mappers.RelationMapper;
import com.youzi.blue.server.mappers.UserMapper;
import com.youzi.blue.server.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("UserService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    CommonMapper commonMapper;
    @Autowired
    RelationMapper relationMapper;

    @Override
    public User auth(String username, String password) {
        return getUserByUsername(username);
    }

    @Override
    public User register(User user) {
        User one = getUserByUsername(user.getUsername());
        if (one != null) {
            return null;
        } else {
            user.setId(UuidUtil.get32UUID());
            user.setWatchToken(UuidUtil.randomNumber(6));
            baseMapper.insert(user);
            return user;
        }
    }

    @Override
    public Integer updateDevice(String username, String watchUser, String token) {
        //判断用户存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, watchUser);
        User user = baseMapper.selectOne(queryWrapper);

        if (user == null) {
            return 1;
        }

        //判断关系存在
        LambdaQueryWrapper<Relation> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Relation::getUsername, username);
        queryWrapper1.eq(Relation::getWatchUser, watchUser);
        Relation one = relationMapper.selectOne(queryWrapper1);
        if (one == null) {
            //关系不在则添加
            if (!token.equals(user.getWatchToken())) {
                return 2;
            }
            Relation relation = new Relation(UuidUtil.get32UUID(), username, watchUser, 1);
            relationMapper.insert(relation);
            return 3;
        } else {
            //关系在则修改
            if (!token.equals(user.getWatchToken())) {
                return 2;
            }
            if (one.getPermit() == 1) {
                //已有权限，直接返回不更新
                return 4;
            }
            one.setPermit(1);
            relationMapper.updateById(one);
            //更新成功
            return 5;
        }
    }

    @Override
    public void deleteWatchUser(String username, String watchUser) {
        LambdaQueryWrapper<Relation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Relation::getUsername, username);
        queryWrapper.eq(Relation::getWatchUser, watchUser);
        relationMapper.delete(queryWrapper);
    }

    @Override
    public String getToken(String username) {
        return commonMapper.getToken(username);
    }

    @Override
    public String updateToken(String username, String token) {
        if (token == null) {
            //没有令牌则随机生成
            token = UuidUtil.randomNumber(6);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = baseMapper.selectOne(queryWrapper);

        if (!user.getWatchToken().equals(token)) {
            //令牌变化，之前权限全部取消， 需要重新验证
            commonMapper.updatePermit(username, 0);
        }

        commonMapper.updateToken(username, token);
        return token;
    }

    @Override
    public List<Map> device(String userName) {
        return commonMapper.devices(userName);
    }

    @Override
    public void updateUser(User user) {
        baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
    }

    @Override
    public void updateUserState(String userId, Integer state) {
        User user = new User(userId, state);
        baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        ArrayList<HashMap<String, Object>> userPermitList = commonMapper.getRelativeUser(userId);
        //通知数据改变
        HashMap<String, Object> map = new HashMap<>();
        map.put("state", state);
        map.put("username", userId);
        ChannelManager.notifyStateChange(userPermitList, userId,state);
    }

    @Override
    public Boolean isPermit(String username, String watchUser) {
        LambdaQueryWrapper<Relation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Relation::getUsername, username);
        queryWrapper.eq(Relation::getWatchUser, watchUser);
        Relation relation = relationMapper.selectOne(queryWrapper);
        return relation != null && relation.getPermit() != 0;
    }


    private User getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return baseMapper.selectOne(queryWrapper);
    }
}
