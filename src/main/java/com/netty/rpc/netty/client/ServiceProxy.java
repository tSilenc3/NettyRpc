package com.netty.rpc.netty.client;

import com.netty.rpc.netty.codec.RpcRequest;
import com.netty.rpc.netty.codec.RpcResponse;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class ServiceProxy {

    public static ServiceProxy getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;
        private ServiceProxy instance;

        Singleton() {
            this.instance = new ServiceProxy();
        }

        public ServiceProxy getInstance() {
            return instance;
        }
    }

    public Object create(Class cls) {
        return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{ cls }, new ProxyInvocationHandler());
    }

    private class ProxyInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {

            RpcRequest request = new RpcRequest();
            String uid = UUID.randomUUID().toString();
            request.setRequestId(uid);
            request.setClassName(method.getDeclaringClass().getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setMethodName(method.getName());

            Channel channel = null;
            try {
                channel = NettyChannelPool.getInstance().getChannel();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CallBackService callback = new CallBackService();

            ChannelUtil.setChannelCallBack(channel, uid, callback);

            try {
                synchronized(callback) {
                    channel.writeAndFlush(request);
                    callback.wait();

                    RpcResponse response = callback.getResponse();
                    return response.getResult();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
