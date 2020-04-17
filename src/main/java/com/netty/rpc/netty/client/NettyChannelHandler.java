package com.netty.rpc.netty.client;

import com.netty.rpc.netty.codec.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyChannelHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        Channel channel = channelHandlerContext.channel();
        String uid = response.getRequestId();
        CallBackService callback = ChannelUtil.removeChannelCallBack(channel, uid);
        callback.receiveMessage(response);
    }
}
