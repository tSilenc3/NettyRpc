package com.netty.rpc.netty.client;

import com.netty.rpc.netty.codec.RpcResponse;

public class CallBackService {

    private RpcResponse response;

    public synchronized void receiveMessage(RpcResponse response) {
        this.response = response;
        this.notify();
    }

    public RpcResponse getResponse() {
        return response;
    }
}
