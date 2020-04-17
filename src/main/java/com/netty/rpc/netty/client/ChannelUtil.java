package com.netty.rpc.netty.client;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;

public class ChannelUtil {
    public static final AttributeKey<Map<String, Object>> ATTR_KEY = AttributeKey.valueOf("dataMap");


    public static void setChannelCallBack(Channel channel, String uid, CallBackService callBack) {
        channel.attr(ATTR_KEY).get().put(uid, callBack);
    }

    public static <T> T removeChannelCallBack(Channel channel, String uid) {
        return (T) channel.attr(ATTR_KEY).get().remove(uid);
    }
}
