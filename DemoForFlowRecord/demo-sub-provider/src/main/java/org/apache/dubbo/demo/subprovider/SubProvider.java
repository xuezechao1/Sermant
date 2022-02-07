package org.apache.dubbo.demo.subprovider;

import com.huawei.dubbo.common.api.GoodByeService;
import com.huawei.dubbo.common.util.Const;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import java.util.concurrent.CountDownLatch;

/**
 * @author luanwenfei
 * @version 0.0.1
 * @since 2021-04-16
 */
public class SubProvider {
    private static String zookeeperHost = System
            .getProperty("zookeeper.address", Const.ZOOKEEPER_URL);
    private static String zookeeperPort = System.getProperty("zookeeper.port",
            "2181");

    public static void subProvider() throws Exception {
        ServiceConfig<GoodByeService> service = new ServiceConfig<>();
        service.setApplication(new ApplicationConfig("sub-dubbo-provider"));
        service.setRegistry(new RegistryConfig(
                "zookeeper://" + zookeeperHost + ":" + zookeeperPort));
        service.setInterface(GoodByeService.class);
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("");
        protocolConfig.setPort(-1);
        service.setProtocol(protocolConfig);
        service.setRef(new GoodByeServiceImpl());
        service.setGroup("luanwenfei");
        service.setVersion("1.0.1");
        service.setTimeout(3000);
        service.export();
        System.out.println("dubbo sub service started");
        new CountDownLatch(1).await();
    }
}
