package com.heima.common.zookeeper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "zk")
@PropertySource("classpath:zookeeper.properties")
public class ZkConfig {

    private String host;
    private String sequencePath;

    @Bean
    public ZookeeperClient zookeeperClient(){
        return new ZookeeperClient(this.host, this.sequencePath);
    }
}
