package com.youzi.blue.server.handlers;


import com.youzi.blue.common.protocol.Constants;
import com.youzi.blue.common.protocol.Message;
import com.youzi.blue.server.entity.User;
import com.youzi.blue.server.manager.ChannelManager;
import com.youzi.blue.server.service.impl.UserServiceImpl;
import com.youzi.blue.server.util.SpringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class HearterCheckHandler extends IdleStateHandler {
    private static final Message heartBeatMessage = new Message(Message.TYPE.HEARTBEAT);
    private final UserServiceImpl userService = SpringUtils.getBean("UserService");
    public static final int READ_IDLE_TIME = 20;
    public static final int WRITE_IDLE_TIME = 15;

    public HearterCheckHandler() {
        super(READ_IDLE_TIME, WRITE_IDLE_TIME, 0);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws InterruptedException {
        Channel channel = ctx.channel();
        String clientId = channel.attr(Constants.CLIENT_ID).get();
        ChannelId channelId = channel.id();

        if (channel.isWritable()) {
            if (IdleState.WRITER_IDLE == evt.state()) {
                log.info("发送心跳[{}<{}>]", clientId, channelId);
                ctx.channel().writeAndFlush(heartBeatMessage);
            }
        } else {
            userService.updateUserState(clientId, 0);
            ChannelManager.removeChannel(clientId);

            log.info("链路不可写，关闭[{}<{}>]", clientId, channelId);
            channel.close().sync();
        }


        /*if (IdleState.READER_IDLE == evt.state()) {
            Channel channel = ctx.channel();
            channel.close().sync();

            String userid = channel.attr(Constants.CLIENT_ID).get();
            ChannelManager.takeOutChannel(userid);

            UserServiceImpl userService = SpringUtils.getBean("UserService");
            userService.updateUser(new User(userid, 0));
            log.info("客户端关闭[userid={},channelId={}], message: 客户端超时", userid, channel.id());
        }*/
    }
}
