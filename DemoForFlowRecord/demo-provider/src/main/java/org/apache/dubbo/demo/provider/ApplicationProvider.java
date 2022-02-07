/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.demo.provider;

import com.huawei.dubbo.common.api.GreetingsService;
import com.huawei.dubbo.common.util.Const;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import java.util.concurrent.CountDownLatch;

public class ApplicationProvider {
    private static String zookeeperHost = System
            .getProperty("zookeeper.address", Const.ZOOKEEPER_URL);
    private static String zookeeperPort = System.getProperty("zookeeper.port",
            "2181");

    public static void provider() throws Exception {
        ServiceConfig<GreetingsService> service = new ServiceConfig<>();
        service.setApplication(new ApplicationConfig("first-dubbo-provider"));
        service.setRegistry(new RegistryConfig(
                "zookeeper://" + zookeeperHost + ":" + zookeeperPort));
        service.setInterface(GreetingsService.class);
        service.setRef(new GreetingsServiceImpl());
        service.setGroup("luanwenfei");
        service.setVersion("1.0.1");
        service.setTimeout(3000);
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("");
        protocolConfig.setPort(-1);
        service.setProtocol(protocolConfig);
        service.export();

        System.out.println("dubbo service started");
        new CountDownLatch(1).await();
    }
}
