package com.netty.rpc.netty.codec;

import java.io.Serializable;

public class RpcResponse implements Serializable {
    private Object result;

    private Throwable error;

    private String requestId;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
