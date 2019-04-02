/*
 * Copyright (c) 2019 maoyan.com
 * All rights reserved.
 *
 */
package com.example.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 在这里编写类的功能描述
 *
 * @author wangkai
 * @created 2019/4/2
 */
@Configuration
public class MyConfig {

    @Bean
    public TransportClient client() throws UnknownHostException{

        InetSocketTransportAddress node = new InetSocketTransportAddress(
                InetAddress.getByName("localhost"),
                9300
        );

        Settings settings = Settings.builder()
                .put("cluster.name", "docker-cluster")
                .build();

        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(node);
        return client;


    }
}