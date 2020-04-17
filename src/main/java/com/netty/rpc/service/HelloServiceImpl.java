package com.netty.rpc.service;

import com.netty.rpc.netty.core.RpcService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
