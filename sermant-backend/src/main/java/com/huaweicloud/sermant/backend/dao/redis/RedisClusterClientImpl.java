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

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * redis集群客户端
 *
 * @author xuezechao
 * @since 2023-03-02
 */
public class RedisClusterClientImpl implements EventDao {

    private JedisCluster jedisCluster;

    /**
     * redis 集群
     *
     * @param backendConfig 事件配置
     */
    public RedisClusterClientImpl(BackendConfig backendConfig) {
        Set<HostAndPort> nodes = new HashSet<>();
        String[] addressList = backendConfig.getUrl().split(";");
        for (String address : addressList) {
            nodes.add(new HostAndPort(
                    Arrays.asList(address.split(":")).get(0),
                    Integer.parseInt(Arrays.asList(address.split(":")).get(1))));
        }
        jedisCluster = new JedisCluster(nodes, backendConfig.getUser(), backendConfig.getPassword());
    }

    @Override
    public boolean addEvent(Event event) {
        return false;
    }

    @Override
    public boolean addInstanceMeta(InstanceMeta agentInstanceMeta) {
        return false;
    }

    @Override
    public List<QueryResultEventInfoEntity> queryEvent(EventsRequestEntity eventsRequestEntity) {
        return Collections.emptyList();
    }

    @Override
    public List<QueryResultEventInfoEntity> queryEventPage(String sessionId, int page) {
        return Collections.emptyList();
    }

    @Override
    public QueryCacheSizeEntity getQueryCacheSize(EventsRequestEntity eventsRequestEntity) {
        return new QueryCacheSizeEntity();
    }

    @Override
    public void cleanOverDueEventTimerTask() {

    }
}
