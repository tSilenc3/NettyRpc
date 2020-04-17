package com.netty.rpc.netty.client;

import com.netty.rpc.service.HelloService;
import io.netty.channel.*;


public class Client {

    private static Channel channel;

    public static void main(String[] args) {
        HelloService helloService = (HelloService) ServiceProxy.getInstance().create(HelloService.class);
        String result = helloService.sayHello("World");
        System.out.println(result);
    }

}
