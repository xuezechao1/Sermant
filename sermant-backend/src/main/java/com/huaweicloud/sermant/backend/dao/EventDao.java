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

package com.huaweicloud.sermant.backend.dao;

import com.huaweicloud.sermant.backend.entity.InstanceMeta;
import com.huaweicloud.sermant.backend.entity.event.Event;
import com.huaweicloud.sermant.backend.entity.event.EventsRequestEntity;
import com.huaweicloud.sermant.backend.entity.event.QueryCacheSizeEntity;
import com.huaweicloud.sermant.backend.entity.event.QueryResultEventInfoEntity;

import java.util.List;

/**
 * 数据库接口
 *
 * @author xuezechao
 * @since 2023-03-02
 */
public interface EventDao {

    /**
     * 增加事件
     *
     * @param event 事件
     * @return true/false
     */
    boolean addEvent(Event event);

    /**
     * 增加agent实例
     *
     * @param agentInstanceMeta agent 实例
     * @return true/false
     */
    boolean addInstanceMeta(InstanceMeta agentInstanceMeta);

    /**
     * 事件查询
     *
     * @param eventsRequestEntity 查询条件
     * @return 查询结果
     */
    List<QueryResultEventInfoEntity> queryEvent(EventsRequestEntity eventsRequestEntity);

    /**
     * 查询某页数据
     *
     * @param sessionId id
     * @param page 页码
     * @return 查询结果
     */
    List<QueryResultEventInfoEntity> queryEventPage(String sessionId, int page);

    /**
     * 获取查询结果数据量
     *
     * @param eventsRequestEntity 查询参数
     * @return 查询结果数据量
     */
    QueryCacheSizeEntity getQueryCacheSize(EventsRequestEntity eventsRequestEntity);

    /**
     * 清理过期事件
     */
    void cleanOverDueEventTimerTask();
}
