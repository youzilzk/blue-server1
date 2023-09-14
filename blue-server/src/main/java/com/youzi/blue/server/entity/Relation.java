package com.youzi.blue.server.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_relation")
public class Relation {
    @TableId(value = "id")
    private String id;

    private String username;

    private String watchUser;

    private Integer permit;
}
