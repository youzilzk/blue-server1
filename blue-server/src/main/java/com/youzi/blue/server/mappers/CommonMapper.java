package com.youzi.blue.server.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youzi.blue.server.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface CommonMapper extends BaseMapper<User> {
    @Select({"select tu.username,(CASE WHEN tr.permit = 0 THEN 2 ELSE tu.state END)  as state from t_relation as tr left join t_user as tu on tu.username= tr.watch_user where tr.username=#{username}"})
    List<Map> devices(@Param("username") String username);

    @Select({"select watch_token from t_user where username=#{username}"})
    String getToken(@Param("username") String username);

    @Select({"select username,permit from t_relation where watch_user=#{username}"})
    ArrayList<HashMap<String, Object>> getRelativeUser(@Param("username") String username);

    @Select({"update t_user set watch_token=#{token} where username=#{username}"})
    String updateToken(@Param("username") String username, @Param("token") String token);

    @Select({"update t_relation set permit=#{permit} where watch_user=#{watchUser}"})
    String updatePermit(@Param("watchUser") String watchUser, @Param("permit") Integer permit);

    @Select({"update t_relation set permit=#{permit} where username=#{username} and watch_user=#{watchUser}"})
    String updatePermit1(@Param("username") String username, @Param("watchUser") String watchUser, @Param("permit") Integer permit);

}
