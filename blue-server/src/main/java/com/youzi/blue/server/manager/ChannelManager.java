package com.youzi.blue.server.manager;

import com.alibaba.fastjson.JSONObject;
import com.youzi.blue.common.protocol.Message;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author youzi
 */
@Slf4j
public class ChannelManager {


    //<userid:Channel>
    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();


    /**
     * 获取隧道
     *
     * @param userid
     */
    public static Channel getChannel(String userid) {
        return channelMap.get(userid);
    }

    /**
     * 获取在线用户
     */
    public static Set<String> getOnLineUserIds() {
        return channelMap.keySet();
    }

    /**
     * 取出隧道
     *
     * @param userid
     */
    public static Channel takeOutChannel(String userid) {
        Channel channel = channelMap.get(userid);
        channelMap.remove(userid);
        return channel;
    }

    /**
     * 获取隧道
     */
    public static Collection<Channel> getChannel() {
        return channelMap.values();
    }


    /**
     * 更新隧道
     *
     * @param channel
     */
    public static void updateChannel(String userid, Channel channel) {
        channelMap.put(userid, channel);
    }

    /**
     * 删除隧道
     *
     * @param userid
     */
    public static void removeChannel(String userid) {
        channelMap.remove(userid);
    }

    /**
     * 通知状态改变
     *
     * @param userPermitList
     */
    public static void notifyStateChange(ArrayList<HashMap<String, Object>> userPermitList, String userId, Integer state) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("state", state);
        map.put("username", userId);
        //原始数据字节码
        byte[] originalBytes = JSONObject.toJSONString(map).getBytes();

        //无权限数据字节码
        map.put("state", 2);
        byte[] bytesNoPermit = JSONObject.toJSONString(map).getBytes();

        userPermitList.forEach(e -> {
            String username = (String) e.get("username");
            Integer permit = (Integer) e.get("permit");
            Channel channel = channelMap.get(username);
            if (channel != null) {
                log.info("下发状态改变信息[{}[:[{}]", e, map.toString());
                if (state == 1 && permit == 0) {
                    //设备上线且无权限，状态为2-黄色
                    channel.writeAndFlush(new Message(Message.TYPE.DATACHANGE, bytesNoPermit));
                } else {
                    //否则发默认数据
                    channel.writeAndFlush(new Message(Message.TYPE.DATACHANGE, originalBytes));
                }
            }
        });
    }


    /**
     * 关闭隧道
     *
     * @param userid
     */
    public static void close(String userid) throws InterruptedException {
        channelMap.get(userid).close().sync();
        channelMap.remove(userid);
    }

    /**
     * 关闭隧道
     */
    public static void closeAll() {
        channelMap.keySet().forEach(e -> {
            try {
                channelMap.get(e).close().sync();
                channelMap.remove(e);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

    }


}
