package com.netty.rpc.netty.core;

import com.netty.rpc.netty.codec.RpcRequest;
import com.netty.rpc.netty.codec.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.HashMap;

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private HashMap<String, Object> handlerMap;

    public RpcHandler(HashMap<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        try {
            rpcResponse.setResult(handle(rpcRequest));
        } catch (Throwable throwable) {
            rpcResponse.setError( throwable);
        }
        channelHandlerContext.channel().writeAndFlush(rpcResponse);
    }

    private Object handle(RpcRequest request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);

        String methodName = request.getMethodName();
        Class<?>[] parameterType = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Method method = serviceBean.getClass().getMethod(methodName, parameterType);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
