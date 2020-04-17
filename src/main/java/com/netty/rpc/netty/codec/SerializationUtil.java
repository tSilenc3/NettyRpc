package com.netty.rpc.netty.codec;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;

public class SerializationUtil {

    public static <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(obj);
        outputStream.flush();
        outputStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    public static <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        Object obj  = inputStream.readObject();
        inputStream.close();
        return (T) obj;
    }

}
