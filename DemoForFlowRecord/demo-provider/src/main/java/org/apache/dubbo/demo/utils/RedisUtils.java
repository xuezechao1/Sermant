package org.apache.dubbo.demo.utils;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author luanwenfei
 * @version 0.0.1
 * @since 2021-05-13
 */
public class RedisUtils {
    private static RedissonClient client = null;

    public static RedissonClient getClient() {
        if (client == null) {
            synchronized (RedisUtils.class) {
                if (client == null) {
                    Config config = new Config();
                    config.useSingleServer().setAddress("redis://127.0.0.1:6379");
//                    config.useClusterServers()
//                            .setScanInterval(3000)
//                            .addNodeAddress("redis://127.0.0.1:6379");
//                            .addNodeAddress("redis://122.44.184.172:6379","redis://122.44.184.172:6380")
//                            .addNodeAddress("redis://122.44.184.200:6379","redis://122.44.184.200:6380")
//                            .addNodeAddress("redis://122.44.184.111:6379","redis://122.44.184.111:6380");
                    client = Redisson.create(config);
                }
            }
        }
        return client;
    }
}
