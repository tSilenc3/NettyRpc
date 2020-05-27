package com.netty.rpc.netty.core;

import com.netty.rpc.netty.client.NettyChannelPool;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ServiceDiscover {

    String serviceName;

    String registryAddress;

    public static volatile ArrayList<String> serviceList;

    CountDownLatch countDownLatch = new CountDownLatch(1);

    public ServiceDiscover() {
        try {
            Resource resource = new ClassPathResource("application.properties");
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            serviceName = props.getProperty("spring.application.name");
            registryAddress = props.getProperty("registry.address");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void discover() {
        ZooKeeper zooKeeper = connectZooKeeper();

        if (zooKeeper != null) {
            discoverNode(zooKeeper);
        }
    }

    private ZooKeeper connectZooKeeper() {
        ZooKeeper zooKeeper = null;

        try {
            zooKeeper = new ZooKeeper(registryAddress, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        countDownLatch.countDown();
                    }
                }
            });

            countDownLatch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return zooKeeper;
    }

    private void discoverNode(ZooKeeper zooKeeper) {
        try {
            List<String> nodeList = zooKeeper.getChildren("/" + serviceName, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                        discoverNode(zooKeeper);
                        NettyChannelPool.getInstance().reload();
                    }
                }
            });

            ArrayList<String> dataList = new ArrayList<>();

            for (String node : nodeList) {
                byte[] data = zooKeeper.getData("/" + serviceName + "/" + node, false, null);
                dataList.add(new String(data));
            }
            serviceList = dataList;
        }catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private enum Singleton {
        INSTANCE;

        private ServiceDiscover instance;

        Singleton() {
            this.instance = new ServiceDiscover();
        }

        public ServiceDiscover getInstance() {
            return instance;
        }
    }

    public static ServiceDiscover getInstance() {
        return Singleton.INSTANCE.getInstance();
    }
}
