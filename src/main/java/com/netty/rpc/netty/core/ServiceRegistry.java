package com.netty.rpc.netty.core;

import org.apache.zookeeper.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Component
public class ServiceRegistry {

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Value("${registry.address}")
    private String registryAddress;

    @Value("${spring.application.name}")
    private String applicationName;

    public ServiceRegistry() {

    }

    public void registry(String node) {
        if (node != null) {
            ZooKeeper zooKeeper = connectZooKeeper();

            if (zooKeeper != null) {
                createNode(zooKeeper, node);
            }
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

    private void createNode(ZooKeeper zooKeeper , String node) {
        try {
            byte[] data = node.getBytes();

            if (zooKeeper.exists("/" + applicationName, false) == null) {
                zooKeeper.create("/" + applicationName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            zooKeeper.create("/" + applicationName + "/data", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
