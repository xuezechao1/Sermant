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

package org.apache.dubbo.demo.client;

import com.huawei.dubbo.common.api.GreetingsService;
import com.huawei.dubbo.common.domain.User;
import com.huawei.dubbo.common.util.Const;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class ApplicationClient {
    private static String zookeeperHost = System
            .getProperty("zookeeper.address", Const.ZOOKEEPER_URL);
    private static String zookeeperPort = System.getProperty("zookeeper.port",
            "2181");

    public static void main(String[] args) throws InterruptedException {
        ReferenceConfig<GreetingsService> reference = new ReferenceConfig<>();
        reference.setApplication(new ApplicationConfig("first-dubbo-consumer"));
        reference.setRegistry(new RegistryConfig(
                "zookeeper://" + zookeeperHost + ":" + zookeeperPort));
        reference.setInterface(GreetingsService.class);
        reference.setGroup("luanwenfei");
        reference.setVersion("1.0.1");
        reference.setTimeout(3000);
        GreetingsService service = reference.get();
        int i = 0;
        while (true) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                User user = new User("Test_" + i, "16", simpleDateFormat.format(new Date()));
                System.out.println(service.sayHi(String.valueOf(i)));
                System.out.println(service.sayHiUser(user));
                System.out.println(service.count(i));
//                service.testMysql(user);
//                System.out.println(Arrays.toString(service.testRedis()));
//                System.out.println(service.testCustom(user));
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
            Thread.sleep(5000);
            i++;
        }
    }
}