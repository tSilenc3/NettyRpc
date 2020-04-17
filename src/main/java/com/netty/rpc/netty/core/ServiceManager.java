package com.netty.rpc.netty.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceManager implements ApplicationContextAware {
    private HashMap<String, Object> serviceMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(RpcService.class);

        for (Map.Entry<String, Object> serviceBean : map.entrySet()) {
            String interfaceName = serviceBean.getValue().getClass().getAnnotation(RpcService.class).value().getName();
            serviceMap.put(interfaceName, serviceBean.getValue());
        }
    }

    public HashMap<String, Object> getServiceMap() {
        return serviceMap;
    }
}
