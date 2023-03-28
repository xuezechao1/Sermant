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

import com.huaweicloud.sermant.backend.common.conf.BackendConfig;
import com.huaweicloud.sermant.backend.dao.EventDao;
import com.huaweicloud.sermant.backend.entity.InstanceMeta;
import com.huaweicloud.sermant.backend.entity.event.Event;
import com.huaweicloud.sermant.backend.entity.event.EventsRequestEntity;
import com.huaweicloud.sermant.backend.entity.event.QueryCacheSizeEntity;
import com.huaweicloud.sermant.backend.entity.event.QueryResultEventInfoEntity;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * redis数据库数据处理
 *
 * @author xuezechao
 * @since 2023-03-02
 */
@Component
@EnableScheduling
public class EventDaoForRedis implements EventDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventDaoForRedis.class);

    private EventDao jedis;

    /**
     * 构造函数
     *
     * @param backendConfig 配置
     */
    public EventDaoForRedis(BackendConfig backendConfig) {
        try {
            List<String> redisAddress = Arrays.asList(backendConfig.getUrl().split(";"));
            if (redisAddress.size() == 1) {
                jedis = new RedisClientImpl(backendConfig);
            } else if (redisAddress.size() > 1) {
                jedis = new RedisClusterClientImpl(backendConfig);
            }
        } catch (JedisConnectionException | JedisDataException e) {
            LOGGER.error("connect redis failed, error message: {}", e.getMessage());
        }
    }

    /**
     * 插入事件
     *
     * @param event 事件
     * @return true/false
     */
    @Override
    public boolean addEvent(Event event) {
        return jedis.addEvent(event);
    }

    /**
     * 插入agent实例
     *
     * @param instanceMeta agent元数据
     * @return true/false
     */
    @Override
    public boolean addInstanceMeta(InstanceMeta instanceMeta) {
        return jedis.addInstanceMeta(instanceMeta);
    }

    /**
     * 事件查询
     *
     * @param eventsRequestEntity 查询条件
     * @return 查询结果
     */
    @Override
    public List<QueryResultEventInfoEntity> queryEvent(EventsRequestEntity eventsRequestEntity) {
        return jedis.queryEvent(eventsRequestEntity);
    }

    /**
     * 获取某一页数据
     *
     * @param page 页码
     * @return 查询数据
     */
    @Override
    public List<QueryResultEventInfoEntity> queryEventPage(String sessionId, int page) {
        return jedis.queryEventPage(sessionId, page);
    }

    /**
     * 获取查询数据类型数量
     *
     * @return 查询结果数量
     */
    @Override
    public QueryCacheSizeEntity getQueryCacheSize(EventsRequestEntity eventsRequestEntity) {
        return jedis.getQueryCacheSize(eventsRequestEntity);
    }

    @Override
    @Scheduled(fixedDelayString = "${database.fixedDelay}")
    public void cleanOverDueEventTimerTask() {
        jedis.cleanOverDueEventTimerTask();
    }
}
