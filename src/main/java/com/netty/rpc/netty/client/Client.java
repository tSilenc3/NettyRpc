package com.netty.rpc.netty.client;

import com.netty.rpc.service.HelloService;


public class Client {


    public static void main(String[] args) {
        HelloService helloService = (HelloService) ServiceProxy.getInstance().create(HelloService.class);
        String result = helloService.sayHello("Hello");
        System.out.println(result);
    }

}
