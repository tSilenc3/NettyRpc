package com.netty.rpc.service;

import com.netty.rpc.netty.core.RpcService;
import org.springframework.stereotype.Component;

@RpcService(HelloService.class)
@Component
public interface HelloService {
    String sayHello(String name);
}
