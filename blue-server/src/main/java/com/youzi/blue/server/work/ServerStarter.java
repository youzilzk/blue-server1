package com.youzi.blue.server.work;

import com.youzi.blue.common.protocol.MessageDecoder;
import com.youzi.blue.common.protocol.MessageEncoder;
import com.youzi.blue.server.config.ServerProperties;
import com.youzi.blue.server.handlers.HearterCheckHandler;
import com.youzi.blue.server.handlers.ServerChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * 服务启动器
 */
@Slf4j
public class ServerStarter {
    public static void start() {
        new ServerBootstrap().group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        if (ServerProperties.getInstance().isSslEnable()) {
                            SSLContext sslContext = SslContextCreator.createSSLContext();
                            SSLEngine sslEngine = sslContext.createSSLEngine();
                            sslEngine.setUseClientMode(false);
                            sslEngine.setNeedClientAuth(ServerProperties.getInstance().isSslNeedsClientAuth());

                            ch.pipeline().addLast("ssl", new SslHandler(sslEngine));
                        }
                        ch.pipeline().addLast(new MessageDecoder());
                        ch.pipeline().addLast(new MessageEncoder());
                        ch.pipeline().addLast(new HearterCheckHandler());
                        ch.pipeline().addLast(new ServerChannelHandler());
                    }
                }).bind(ServerProperties.getInstance().getPort());
    }
}
