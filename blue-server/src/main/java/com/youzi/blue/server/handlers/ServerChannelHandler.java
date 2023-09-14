package com.youzi.blue.server.handlers;


import com.youzi.blue.common.protocol.Constants;
import com.youzi.blue.common.protocol.Message;
import com.youzi.blue.server.entity.User;
import com.youzi.blue.server.manager.ChannelManager;
import com.youzi.blue.server.service.impl.UserServiceImpl;
import com.youzi.blue.server.util.SpringUtils;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 服务端处理器
 */
@Slf4j
public class ServerChannelHandler extends SimpleChannelInboundHandler<Message> {
    private final UserServiceImpl userService = SpringUtils.getBean("UserService");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws InterruptedException {
        switch (message.getType()) {
            case HEARTBEAT:
                handleHeartbeatMessage(ctx, message);
                break;
            case LINK:
                handleLinkMessage(ctx, message);
                break;
            case RELEVANT:
                handleRelevantMessage(ctx, message);
                break;
            case RELAY:
                handleTransferMessage(ctx, message);
                break;
            case STARTRECORD:
                handleStartRecordMessage(ctx, message);
                break;
            case STOPRECORD:
                handleSopRecordMessage(ctx, message);
                break;
            case CLOSECHANNEL:
                handleCloseChannelMessage(ctx, message);
                break;
            default:
                break;
        }
    }

    private void handleTransferMessage(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();

        String clientId = channel.attr(Constants.CLIENT_ID).get();
        ChannelId channelId = channel.id();
        ConcurrentLinkedDeque<Channel> channels = channel.attr(Constants.SEND_CHANNELS).get();

        if (channels.isEmpty()) {
            channel.writeAndFlush(new Message(Message.TYPE.STOPRECORD, Constants.STATE.REQUEST.value));
            log.info("[{}<{}>]已无观察者，服务器向该客户端下发停止录屏命令！", clientId, channelId);
        } else {
            //转发数据
            int length = message.getData().length;

            for (Channel toChannel : channels) {

                String toClientId = toChannel.attr(Constants.CLIENT_ID).get();
                ChannelId toChannelId = toChannel.id();

                if (toChannel.isWritable()) {
                    log.info("[{}<{}>]->[{}<{}>]数据长度:{}bytes", clientId, channelId, toClientId, toChannelId, length);
                    //转发消息
                    toChannel.writeAndFlush(message);
                } else if (!toChannel.isActive()) {
                    log.info("观察链路失活[{}<{}>]，将移除！", toClientId, toChannelId);
                    channels.remove(toChannel);
                }
            }
        }
    }

    /**
     * 请求录屏，（已提前关联管道，流程为：关联管道->请求录屏）
     */
    private void handleStartRecordMessage(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();
        if (message.getContent().equals(Constants.STATE.REQUEST.value)) {
            Channel watchChannel = channel.attr(Constants.WATCH_CHANNEL).get();
            if (watchChannel != null) {
                log.info("发送录屏命令[{}<{}>]->[{}<{}>]！", channel.attr(Constants.CLIENT_ID).get(), channel.id(), watchChannel.attr(Constants.CLIENT_ID).get(), watchChannel.id());
                watchChannel.writeAndFlush(message);
            }
        }
    }

    /**
     * 请求停止录屏（流程为：取消关联管道->发送停止录屏命令）
     */
    private void handleSopRecordMessage(ChannelHandlerContext ctx, Message message) {
        if (message.getContent().equals(Constants.STATE.REQUEST.value)) {
            Channel channel = ctx.channel();
            ChannelId channelId = channel.id();
            String clientId = channel.attr(Constants.CLIENT_ID).get();

            Channel watchChannel = channel.attr(Constants.WATCH_CHANNEL).get();
            ChannelId watchChannelId = watchChannel.id();
            String watchClientId = watchChannel.attr(Constants.CLIENT_ID).get();

            //本管道移除观察
            channel.attr(Constants.WATCH_CHANNEL).set(null);
            log.info("停止观察[{}<{}>]->[{}<{}>]！", clientId, channelId, watchClientId, watchChannelId);

            //让发送端管道移除本观察者
            ConcurrentLinkedDeque<Channel> sendChannels = watchChannel.attr(Constants.SEND_CHANNELS).get();
            sendChannels.remove(channel);
            log.info("停止发送录屏数据[{}<{}>]->[{}<{}>]！", watchClientId, watchChannelId, clientId, channelId);

            //如果发送端发送队列为空，说明发送端已无观察者， 则停止录屏
            if (sendChannels.isEmpty()) {
                watchChannel.writeAndFlush(message);
                log.info("[{}<{}>]已无观察者，服务器向该客户端下发停止录屏命令！", watchClientId, watchChannel.id());
            }
        }
    }

    /**
     * 客户端连接
     */
    private void handleLinkMessage(ChannelHandlerContext ctx, Message message) throws InterruptedException {
        String userid = new String(message.getData());
        Channel channel = ctx.channel();

        //有可能手机断网，整个网络链路上的设备并未检查出链路失活，导致服务器一直发送激活心跳浪费资源，这种情况让新连接到来则主动关闭
        Channel takeOutChannel = ChannelManager.takeOutChannel(userid);
        if (takeOutChannel != null) {
            takeOutChannel.close().sync();
        }

        channel.attr(Constants.CLIENT_ID).set(userid);
        //初始化数据发送Channel空队列， 观看请求时绑定需要判空太麻烦， 不如先初始化
        channel.attr(Constants.SEND_CHANNELS).set(new ConcurrentLinkedDeque<>());

        log.info("客户端连接[{}<{}>]", userid, channel.id());

        userService.updateUserState(userid, 1);
        ChannelManager.updateChannel(userid, ctx.channel());
    }

    /**
     * 关联管道
     */
    private void handleRelevantMessage(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();
        String watchUser = new String(message.getData());
        String userId = channel.attr(Constants.CLIENT_ID).get();

        Channel toChannel = ChannelManager.getChannel(watchUser);

        Boolean permit = userService.isPermit(userId, watchUser);
        if (permit) {
            log.info("关联管道[{}<{}>]<->[{}<{}>]！", userId, channel.id(), watchUser, toChannel.id());
            channel.attr(Constants.WATCH_CHANNEL).set(toChannel);
            toChannel.attr(Constants.SEND_CHANNELS).get().add(channel);
        } else {
            log.info("关联管道无权限[{}<{}>]<->[{}<{}>]！", userId, channel.id(), watchUser, toChannel.id());
        }

    }

    /**
     * 回复心跳
     */
    private void handleHeartbeatMessage(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();

        //心跳改为服务端下发， 此处已无用
        log.info("回复心跳[{}<{}>]", channel.attr(Constants.CLIENT_ID).get(), channel.id());
        ctx.channel().writeAndFlush(message);
    }

    /**
     * 关闭链路
     */
    private void handleCloseChannelMessage(ChannelHandlerContext ctx, Message message) throws InterruptedException {
        Channel channel = ctx.channel();
        String clientId = channel.attr(Constants.CLIENT_ID).get();

        //心跳改为服务端下发， 此处已无用
        log.info("客户端息屏断网[{}<{}>]", clientId, channel.id());

        userService.updateUserState(clientId, 0);
        ChannelManager.removeChannel(clientId);
        channel.close().sync();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        ChannelId channelId = channel.id();
        String clientId = channel.attr(Constants.CLIENT_ID).get();

        //1.检查本异常管道是否正在观察，是则让对方移除自己
        Channel watchChannel = channel.attr(Constants.WATCH_CHANNEL).get();

        if (watchChannel != null) {
            ChannelId watchChannelId = watchChannel.id();
            String watchClientId = watchChannel.attr(Constants.CLIENT_ID).get();

            ConcurrentLinkedDeque<Channel> sendChannels = watchChannel.attr(Constants.SEND_CHANNELS).get();
            sendChannels.remove(channel);
            log.warn("由于管道[{}<{}>]出现异常， 其正在观察[{}<{}>], 因此让对方停止发送录屏数据！", clientId, channelId, watchClientId, watchChannelId);
            //如果发送端发送队列为空，说明发送端已无观察者， 则停止录屏
            if (sendChannels.isEmpty()) {
                if (watchChannel.isWritable()) {
                    watchChannel.writeAndFlush(new Message(Message.TYPE.STOPRECORD, Constants.STATE.REQUEST.value));
                    log.info("[{}<{}>]已无观察者，服务器向该客户端下发停止录屏命令！", watchClientId, watchChannelId);
                }
            }
        }
        userService.updateUserState(clientId, 0);
        ChannelManager.removeChannel(clientId);

        //2.本管道为发送录屏数据者则直接关闭，此时将抛弃此管道
        if (channel.isWritable()) {
            //入股管道还可写，命令客户端重连
            Message message = new Message(Message.TYPE.RECONNECTED, Constants.STATE.REQUEST.value);
            channel.writeAndFlush(message);
        }
        channel.close().sync();
        log.info("关闭异常客户端[{}<{}>], message: {}", clientId, channelId, cause.getMessage());
    }
}