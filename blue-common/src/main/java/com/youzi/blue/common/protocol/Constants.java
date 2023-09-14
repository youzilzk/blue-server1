package com.youzi.blue.common.protocol;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.var;

import java.util.concurrent.ConcurrentLinkedDeque;

public class Constants {

    /**
     * 数据发送的Channels
     */
    public static AttributeKey<ConcurrentLinkedDeque<Channel>> SEND_CHANNELS = AttributeKey.newInstance("send_channels");

    /**
     * 观看的Channel
     */
    public static AttributeKey<Channel> WATCH_CHANNEL = AttributeKey.newInstance("watch_channel");
    /**
     * 客户端id
     */
    public static AttributeKey<String> CLIENT_ID = AttributeKey.newInstance("client_id");

    public enum STATE {
        SUCCESS("200"),
        FAILED("500"),
        CHECK("400"),
        REQUEST("300");

        public String value;

        STATE(String value) {
            this.value = value;
        }

        public Constants.STATE byValue(String value) {
            for (var handleEnum : values()) {
                if (handleEnum.value.equals(value)) {
                    return handleEnum;
                }
            }
            throw new RuntimeException("enum value undefined.");
        }

    }
}
