package com.youzi.blue.server.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youzi.blue.server.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
