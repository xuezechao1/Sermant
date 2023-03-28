/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.sermant.backend.dao.redis;

import com.huaweicloud.sermant.backend.common.conf.CommonConst;
import com.huaweicloud.sermant.backend.common.conf.BackendConfig;
import com.huaweicloud.sermant.backend.entity.ClusterEntity;
import com.huaweicloud.sermant.backend.entity.EnvironmentEntity;
import com.huaweicloud.sermant.backend.entity.InstanceMeta;
import com.huaweicloud.sermant.backend.entity.NodeEntity;
import com.huaweicloud.sermant.backend.entity.event.Event;
import com.huaweicloud.sermant.backend.entity.event.EventInfo;
import com.huaweicloud.sermant.backend.entity.event.EventLevel;
import com.huaweicloud.sermant.backend.entity.event.EventType;
import com.huaweicloud.sermant.backend.entity.event.EventsRequestEntity;
import com.huaweicloud.sermant.backend.entity.event.LogInfo;

import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

/**
 * EventDaoForRedis Tester.
 *
 * @author xuezechao
 * @since 2023-03-14
 */
@PrepareForTest(EventDaoForRedis.class)
@RunWith(PowerMockRunner.class)
public class EventDaoForRedisTest {
    BackendConfig backendConfig = new BackendConfig();
    Jedis jedis;
    Event event = new Event();
    InstanceMeta instanceMeta = new InstanceMeta();
    EventDaoForRedis eventDaoForRedis;
    EventsRequestEntity eventsRequestEntity = new EventsRequestEntity();

    @Before
    public void before() throws Exception {
        backendConfig.setUrl("");
        backendConfig.setPassword("");
        jedis = Mockito.mock(Jedis.class);
        PowerMockito.whenNew(Jedis.class).withAnyArguments().thenReturn(jedis);
        eventDaoForRedis = new EventDaoForRedis(backendConfig);
        eventsRequestEntity.setStartTime(new Date().getTime());

        event.setTime(new Date().getTime());
        event.setScope("scope");
        event.setMetaHash("hashMeat");
        event.setEventLevel(EventLevel.NORMAL);

        instanceMeta.setInstanceId("instanceId");
        instanceMeta.setService("application");
        instanceMeta.setAz("az");
        NodeEntity nodeEntity = new NodeEntity();
        nodeEntity.setIp("127.0.0.1");
        instanceMeta.setNode(nodeEntity);
        EnvironmentEntity environmentEntity = new EnvironmentEntity();
        environmentEntity.setEnv("env");
        instanceMeta.setEnvironment(environmentEntity);
        ClusterEntity clusterEntity = new ClusterEntity();
        clusterEntity.setCluster("cluster");
        instanceMeta.setCluster(clusterEntity);

        eventsRequestEntity.setEndTime(new Date().getTime());
    }

    @After
    public void after() {
        jedis.close();
    }

    @Test
    public void testOtherType() {
        event.setEventType(EventType.OPERATION);
        EventInfo eventInfo = new EventInfo();
        eventInfo.setName("name");
        eventInfo.setDescription("description");
        event.setEventInfo(eventInfo);
        setReturn(getEventField(instanceMeta, event));

        eventDaoForRedis.addInstanceMeta(instanceMeta);
        Assert.assertTrue(eventDaoForRedis.addInstanceMeta(instanceMeta));
        Assert.assertTrue(eventDaoForRedis.addEvent(event));
    }

    @Test
    public void testLogType() {
        event.setEventType(EventType.LOG);
        LogInfo logInfo = new LogInfo();
        logInfo.setLogLevel("logLevel");
        logInfo.setLogClass("logClass");
        logInfo.setLogMessage("logMessage");
        logInfo.setLogThreadId(1);
        logInfo.setLogMethod("logMethod");
        logInfo.setThrowable(new Throwable());
        event.setLogInfo(logInfo);
        setReturn(getEventField(instanceMeta, event));

        Assert.assertTrue(eventDaoForRedis.addInstanceMeta(instanceMeta));
        Assert.assertTrue(eventDaoForRedis.addEvent(event));
    }

    public String getEventField(InstanceMeta instanceMeta, Event event) {
        return String.join(CommonConst.JOIN_REDIS_KEY,
                instanceMeta.getInstanceId(),
                instanceMeta.getService(),
                instanceMeta.getNode().getIp(),
                instanceMeta.getCluster().getCluster(),
                instanceMeta.getEnvironment().getEnv(),
                instanceMeta.getAz(),
                event.getMetaHash(),
                String.valueOf(event.getEventType().getType()),
                event.getScope(),
                String.valueOf(event.getTime()));
    }

    public void setReturn(String eventField) {
        Mockito.doReturn(1L).when(jedis).hset(CommonConst.REDIS_EVENT_KEY, eventField, JSONObject.toJSONString(event));
        Mockito.doReturn(1L).when(jedis).zadd(CommonConst.REDIS_EVENT_FIELD_SET_KEY, event.getTime(), eventField);
        Mockito.doReturn(1L).when(jedis).hset(CommonConst.REDIS_HASH_KEY_OF_INSTANCE_META,
                instanceMeta.getMetaHash(), JSONObject.toJSONString(instanceMeta));
        Mockito.doReturn(1L).when(jedis).hdel(CommonConst.REDIS_EVENT_KEY, eventField);
        Mockito.doReturn(1L).when(jedis).zrem(CommonConst.REDIS_EVENT_FIELD_SET_KEY, eventField);
        Mockito.doReturn(1L).when(jedis).hdel(CommonConst.REDIS_HASH_KEY_OF_INSTANCE_META, instanceMeta.getMetaHash());
        Mockito.doReturn(JSONObject.toJSONString(instanceMeta)).when(jedis).hget(CommonConst.REDIS_HASH_KEY_OF_INSTANCE_META, event.getMetaHash());
    }
} 
