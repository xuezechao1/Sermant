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


import com.huawei.dubbo.common.api.GoodByeService;
import com.huawei.dubbo.common.api.GreetingsService;
import com.huawei.dubbo.common.domain.User;
import com.huawei.dubbo.common.util.Const;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import org.apache.dubbo.demo.repository.UserDao;
import org.apache.dubbo.demo.utils.RedisUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RFuture;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class GreetingsServiceImpl implements GreetingsService {
    private static String zookeeperHost = System
            .getProperty("zookeeper.address", Const.ZOOKEEPER_URL);
    private static String zookeeperPort = System.getProperty("zookeeper.port",
            "2181");

    private ReferenceConfig<GoodByeService> reference = new ReferenceConfig<>();

    @Override
    public String sayHi(String name) {
        ReferenceConfig<GoodByeService> reference = new ReferenceConfig<>();
        reference.setApplication(new ApplicationConfig("sub-dubbo-consumer"));
        reference.setRegistry(new RegistryConfig(
                "zookeeper://" + zookeeperHost + ":" + zookeeperPort));
        reference.setInterface(GoodByeService.class);
        reference.setGroup("luanwenfei");
        reference.setVersion("1.0.1");
        GoodByeService service = reference.get();
        name = "hi:" + service.sayBye(name);
        reference.destroy();
        return name;
    }

    @Override
    public User sayHiUser(User user) {
        ReferenceConfig<GoodByeService> reference = new ReferenceConfig<>();
        reference.setApplication(new ApplicationConfig("sub-dubbo-consumer"));
        reference.setRegistry(new RegistryConfig(
                "zookeeper://" + zookeeperHost + ":" + zookeeperPort));
        reference.setInterface(GoodByeService.class);
        reference.setGroup("luanwenfei");
        reference.setVersion("1.0.1");
        GoodByeService service = reference.get();
        user = service.sayByeUser(new User("Hi:"+user.getName(),user.getAge(), user.getDate()));
        reference.destroy();
        return user;
    }

    @Override
    public double count(double count) {
        ReferenceConfig<GoodByeService> reference = new ReferenceConfig<>();
        reference.setApplication(new ApplicationConfig("sub-dubbo-consumer"));
        reference.setRegistry(new RegistryConfig(
                "zookeeper://" + zookeeperHost + ":" + zookeeperPort));
        reference.setInterface(GoodByeService.class);
        reference.setGroup("luanwenfei");
        reference.setVersion("1.0.1");
        GoodByeService service = reference.get();
        count = service.count(count);
        reference.destroy();
        return count;
    }

    public void testMysql(User user) {
        UserDao userDao = new UserDao();
        try {
            System.out.println(userDao.addUser(user));
            System.out.println(userDao.findByName(user.getName()));
            System.out.println(userDao.deleteUser(user.getName()));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public String[] testRedis() {
        RAtomicLong rAtomicLong = RedisUtils.getClient().getAtomicLong("demo");
        String[] strings = new String[3];
        for (int i = 0; i < 3; i++) {
            try {
                RFuture<Long> rFuture = rAtomicLong.addAndGetAsync(1);
                rFuture.await();
                System.out.println("redis:" + rFuture.get());
                strings[i] = String.valueOf(rFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());
            }
        }
        return strings;
    }

    @Override
    public List<Object> testCustom(User user) {
        Custom custom = new Custom();
        return custom.createList(user);
    }
}
