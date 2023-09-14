package com.youzi.blue.server.manager;


import io.netty.channel.ChannelFuture;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FutureManager {
    //blueId:ChannelFuture
    private static final Map<String, ChannelFuture> channelFutureMap = new ConcurrentHashMap<>();


    /**
     * 获取ChannelFuture
     *
     * @param blueId
     */
    public static ChannelFuture getChannelFuture(String blueId) {
        return channelFutureMap.get(blueId);
    }

    /**
     * 获取ChannelFuture
     */
    public static Collection<ChannelFuture> getChannelFuture() {
        return channelFutureMap.values();
    }


    /**
     * 更新ChannelFuture
     *
     * @param blueId
     * @param future
     */
    public static void updateChannelFuture(String blueId, ChannelFuture future) {
        channelFutureMap.put(blueId, future);
    }

    /**
     * 删除ChannelFuture
     *
     * @param blueId
     */
    public static void removeChannelFuture(String blueId) {
        channelFutureMap.remove(blueId);
    }

    /**
     * 关闭端口
     *
     * @param blueId
     */
    public static void close(String blueId) throws InterruptedException {
        channelFutureMap.get(blueId).channel().close().sync();
        channelFutureMap.remove(blueId);
    }

    /**
     * 关闭隧道
     */
    public static void closeAll() {
        channelFutureMap.keySet().forEach(e -> {
            try {
                channelFutureMap.get(e).channel().close().sync();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            channelFutureMap.remove(e);
        });

    }
}
