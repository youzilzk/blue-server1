package com.youzi.blue.common.utils;

import java.util.Random;
import java.util.UUID;

public class UuidUtil {

    public UuidUtil() {
    }

    public static String get32UUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    /**
     * @描述: 随机数
     * @出参: String
     * @作者: 李忠坤
     * @日期: 2022-05-15ll
     **/
    public static String randomNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (length > 0) {
            sb.append(random.nextInt(10));
            length--;
        }
        return sb.toString();
    }
}