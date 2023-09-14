package com.youzi.blue.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class User implements Serializable {

    @TableId(value = "id")
    private String id;

    private String username;

    private String password;

    private String icon;

    private String description;

    private String macAddress;

    @TableField(exist = false)
    private String host;

    private Integer state;


    private String watchToken;

    @TableField(exist = false)
    private Channel channel;

    public User(String username, Integer state) {
        this.username = username;
        this.state = state;
    }
}
