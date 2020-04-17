package com.netty.rpc.netty.client;

import com.netty.rpc.netty.codec.RpcDecoder;
import com.netty.rpc.netty.codec.RpcEncoder;
import com.netty.rpc.netty.codec.RpcRequest;
import com.netty.rpc.netty.codec.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class NettyChannelPool {

    public static NettyChannelPool getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        private NettyChannelPool instance;

        Singleton() {
            instance = new NettyChannelPool();
        }

        public NettyChannelPool getInstance() {
            return instance;
        }
    }


    private Channel[] channels;
    private Object[] locks;

    private static final int MAX_CHANNEL_COUNT = 4;

    public NettyChannelPool() {
        channels = new Channel[MAX_CHANNEL_COUNT];
        locks = new Object[MAX_CHANNEL_COUNT];

        for (int i = 0; i < MAX_CHANNEL_COUNT; i++) {
            locks[i] = new Object();
        }
    }

    public Channel getChannel() throws InterruptedException {
        int index = new Random().nextInt(MAX_CHANNEL_COUNT);
        Channel channel = channels[index];

        if (channel != null && channel.isActive()) {
            return channel;
        }

        synchronized (locks[index]) {
            if (channel != null && channel.isActive()) {
                return channel;
            }

            channel = connectServer();
            channels[index] = channel;
        }
        return channel;
    }

    private Channel connectServer() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new RpcEncoder(RpcRequest.class))
                                .addLast(new RpcDecoder(RpcResponse.class))
                                .addLast(new NettyChannelHandler());
                    }
                }).option(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future = bootstrap.connect("localhost", 8000).sync();
        Channel channel = future.channel();

        Attribute<Map<String, Object>> attr = channel.attr(ChannelUtil.ATTR_KEY);
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        attr.set(map);
        return channel;
    }
}
