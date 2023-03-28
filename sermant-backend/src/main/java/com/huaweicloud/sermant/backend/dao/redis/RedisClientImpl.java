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
import com.huaweicloud.sermant.backend.dao.EventDao;
import com.huaweicloud.sermant.backend.entity.InstanceMeta;
import com.huaweicloud.sermant.backend.entity.event.Event;
import com.huaweicloud.sermant.backend.entity.event.EventLevel;
import com.huaweicloud.sermant.backend.entity.event.EventType;
import com.huaweicloud.sermant.backend.entity.event.EventsRequestEntity;
import com.huaweicloud.sermant.backend.entity.event.QueryCacheSizeEntity;
import com.huaweicloud.sermant.backend.entity.event.QueryResultEventInfoEntity;
import com.huaweicloud.sermant.backend.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * redis单机客户端
 *
 * @author xuezechao
 * @since 2023-03-02
 */
public class RedisClientImpl implements EventDao {

    private static final int EVENT_LEVEL_INDEX = 3;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClientImpl.class);

    private JedisPool jedisPool;

    private BackendConfig backendConfig;

    /**
     * 构造redis 连接池
     *
     * @param backendConfig 配置
     */
    public RedisClientImpl(BackendConfig backendConfig) {
        this.backendConfig = backendConfig;
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.parseInt(backendConfig.getMaxTotal()));
        config.setMaxIdle(Integer.parseInt(backendConfig.getMaxIdle()));
        if (backendConfig.getVersion().compareTo("6.0") < 0) {
            jedisPool = new JedisPool(
                    config,
                    Arrays.asList(backendConfig.getUrl().split(CommonConst.REDIS_ADDRESS_SPLIT)).get(0),
                    Integer.parseInt(Arrays.asList(backendConfig.getUrl().split(CommonConst.REDIS_ADDRESS_SPLIT)).get(1)),
                    Integer.parseInt(backendConfig.getTimeout()),
                    backendConfig.getPassword());
        } else {
            jedisPool = new JedisPool(
                    config,
                    Arrays.asList(backendConfig.getUrl().split(CommonConst.REDIS_ADDRESS_SPLIT)).get(0),
                    Integer.parseInt(Arrays.asList(backendConfig.getUrl().split(CommonConst.REDIS_ADDRESS_SPLIT)).get(1)),
                    Integer.parseInt(backendConfig.getTimeout()),
                    backendConfig.getUser(),
                    backendConfig.getPassword());
        }
    }

    @Override
    public boolean addEvent(Event event) {
        try (Jedis jedis = jedisPool.getResource()) {
            String instanceMeta = jedis.hget(CommonConst.REDIS_HASH_KEY_OF_INSTANCE_META, event.getMetaHash());
            if (StringUtils.isEmpty(instanceMeta)) {
                LOGGER.error("add event failed, event:{}, error message:[instance not exist]", event);
                return false;
            }
            InstanceMeta agentInstanceMeta = JSONObject.parseObject(instanceMeta, InstanceMeta.class);

            // 获取事件字段
            String field = getEventField(agentInstanceMeta, event);

            // 检查是否有相同field
            field = field + CommonConst.JOIN_REDIS_KEY + getSameFieldNum(field);

            // 写入事件
            jedis.hset(CommonConst.REDIS_EVENT_KEY, field, getEventStr(event, agentInstanceMeta));

            // 写入类型索引
            jedis.zadd(CommonConst.REDIS_EVENT_FIELD_SET_KEY, event.getTime(), field);
            if (event.getEventLevel().equals(EventLevel.EMERGENCY)) {
                jedis.incrBy(EventLevel.EMERGENCY.toString(), 1);
            } else if (event.getEventLevel().equals(EventLevel.IMPORTANT)) {
                jedis.incrBy(EventLevel.IMPORTANT.toString(), 1);
            } else {
                jedis.incrBy(EventLevel.NORMAL.toString(), 1);
            }
            return true;
        } catch (IllegalStateException e) {
            LOGGER.error("add event failed, event:{}, error message:{}", event, e.getMessage());
            return false;
        }
    }

    /**
     * 事件序列化
     *
     * @param event 事件
     * @param agentInstanceMeta 事件归属的实例
     * @return 事件字符串
     */
    private String getEventStr(Event event, InstanceMeta agentInstanceMeta) {
        QueryResultEventInfoEntity queryResultEventInfoEntity = new QueryResultEventInfoEntity();
        queryResultEventInfoEntity.setTime(event.getTime());
        queryResultEventInfoEntity.setScope(event.getScope());
        queryResultEventInfoEntity.setLevel(event.getEventLevel().toString().toUpperCase(Locale.ROOT));
        queryResultEventInfoEntity.setType(event.getEventType().getDescription());
        if (event.getEventType().getDescription().equals(EventType.LOG.getDescription())) {
            queryResultEventInfoEntity.setInfo(event.getLogInfo());
        } else {
            queryResultEventInfoEntity.setInfo(event.getEventInfo());
        }
        HashMap<String, String> meta = new HashMap<>();
        meta.put("service", agentInstanceMeta.getService());
        meta.put("ip", agentInstanceMeta.getNode().getIp());
        queryResultEventInfoEntity.setMeta(meta);
        return JSONObject.toJSONString(queryResultEventInfoEntity);
    }

    @Override
    public boolean addInstanceMeta(InstanceMeta instanceMeta) {
        try (Jedis jedis = jedisPool.getResource()) {
            String meta = JSONObject.toJSONString(instanceMeta);
            if (StringUtils.isEmpty(
                    jedis.hget(CommonConst.REDIS_HASH_KEY_OF_INSTANCE_META,
                            instanceMeta.getMetaHash()))) {
                // 写入实例信息
                jedis.hset(CommonConst.REDIS_HASH_KEY_OF_INSTANCE_META, instanceMeta.getMetaHash(), meta);
            }
            return true;
        } catch (IllegalStateException e) {
            LOGGER.error("add instance meta failed, instance meta:{}, error message:{}", instanceMeta, e.getMessage());
            return false;
        }
    }

    /**
     * 获取相同field 数量
     *
     * @param field field
     * @return 相同field 数量
     */
    private int getSameFieldNum(String field) {
        int result = 0;
        try (Jedis jedis = jedisPool.getResource()) {
            ScanResult<Map.Entry<String, String>> firstScanResult = jedis.hscan(
                    CommonConst.REDIS_EVENT_KEY,
                    String.valueOf(0),
                    new ScanParams().match(field + "*"));
            int cursor = Integer.parseInt(firstScanResult.getCursor());
            result += firstScanResult.getResult().size();
            while (cursor > 0) {
                ScanResult<Map.Entry<String, String>> scanResult = jedis.hscan(
                        CommonConst.REDIS_EVENT_KEY,
                        String.valueOf(cursor),
                        new ScanParams().match(field + "*"));
                cursor = Integer.parseInt(scanResult.getCursor());
                result += scanResult.getResult().size();
            }
            return result;
        } catch (IllegalStateException e) {
            LOGGER.error( "query same field failed, field:{}, error message:{}", field, e.getMessage());
            return result;
        }
    }

    @Override
    public List<QueryResultEventInfoEntity> queryEvent(EventsRequestEntity eventsRequestEntity) {
        String pattern = getPattern(eventsRequestEntity);
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> queryResultByTime = queryByTimeRange(CommonConst.REDIS_EVENT_FIELD_SET_KEY,
                    eventsRequestEntity.getStartTime(),
                    eventsRequestEntity.getEndTime());
            Collections.reverse(queryResultByTime);
            queryResultByTime = filterQueryResult(queryResultByTime, pattern);
            jedis.setex(
                    eventsRequestEntity.getSessionId(),
                    Integer.parseInt(backendConfig.getSessionTimeout()),
                    JSONObject.toJSONString(queryResultByTime));
            return queryEventPage(eventsRequestEntity.getSessionId(), 1);
        } catch (IllegalStateException e) {
            LOGGER.error("query event failed, error message:{}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 过滤查询结果
     *
     * @param queryResultByTime 按时间查询结果
     * @param pattern 过滤规则
     * @return 过滤结果
     */
    private List<String> filterQueryResult(List<String> queryResultByTime, String pattern) {
        List<String> fanList = new ArrayList<>();
        if (queryResultByTime.size() <= 0) {
            return fanList;
        }
        int threadSize = Integer.parseInt(backendConfig.getFilterThreadNum());
        int dataSize = queryResultByTime.size();
        int threadNum = 0;
        if (dataSize % threadSize == 0) {
            threadNum = dataSize / threadSize;
        } else {
            threadNum = dataSize / threadSize + 1;
        }
        ExecutorService exc = Executors.newFixedThreadPool(threadNum);
        List<Callable<List<String>>> tasks = new ArrayList<>();
        Callable<List<String>> task = null;
        List<String> cutList = null;
        for (int i = 0; i < threadNum; i++) {

            // 切割list
            if (i == threadNum - 1) {
                cutList = queryResultByTime.subList(i * threadSize, queryResultByTime.size());
            } else {
                cutList = queryResultByTime.subList(i * threadSize, (i + 1) * threadSize);
            }

            final List<String> finalCutList = cutList;
            task = new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    List<String> newList = new ArrayList<>();
                    for (String a : finalCutList) {
                        if (a.matches(pattern)) {
                            newList.add(a);
                        }
                    }
                    return newList;
                }
            };
            tasks.add(task);
        }

        try {
            List<Future<List<String>>> results = exc.invokeAll(tasks);
            for (Future<List<String>> result : results) {
                fanList.addAll(result.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("filter query result failed, error message:{}", e.getMessage());
        }
        exc.shutdown();
        return fanList;
    }

    /**
     * 按时间范围查询事件
     *
     * @param key 集合key
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 查询结果
     */
    public List<String> queryByTimeRange(String key, long startTime, long endTime) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, startTime, endTime);
        } catch (IllegalStateException e) {
            LOGGER.error("query event by time failed, key:{}, startTime:{}, endTime:{}, error message:{}",
                    key, startTime, endTime, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<QueryResultEventInfoEntity> queryEventPage(String sessionId, int page) {
        List<QueryResultEventInfoEntity> result = new ArrayList<>();
        try (Jedis jedis = jedisPool.getResource()) {
            String events = jedis.get(sessionId);
            if (!StringUtils.isEmpty(events)) {
                List<String> keyList = JSONObject.parseArray(events, String.class);
                int startIndex = (page - 1) * CommonConst.DEFAULT_PAGE_SIZE;
                int endIndex = Math.min(startIndex + CommonConst.DEFAULT_PAGE_SIZE, keyList.size());
                for (String key : keyList.subList(startIndex, endIndex)) {
                    result.add(JSONObject.parseObject(
                            jedis.hget(CommonConst.REDIS_EVENT_KEY, key),
                            QueryResultEventInfoEntity.class));
                }
            }
            return result;
        } catch (IllegalStateException e) {
            LOGGER.error("query event by page failed, sessionId:{}, error message:{}",
                    sessionId, e.getMessage());
            return result;
        }
    }

    @Override
    public QueryCacheSizeEntity getQueryCacheSize(EventsRequestEntity eventsRequestEntity) {
        QueryCacheSizeEntity queryCacheSize = new QueryCacheSizeEntity();
        try (Jedis jedis = jedisPool.getResource()) {
            queryCacheSize.setEmergencyNum(StringUtils.filterStr(jedis.get(EventLevel.EMERGENCY.toString())));
            queryCacheSize.setImportantNum(StringUtils.filterStr(jedis.get(EventLevel.IMPORTANT.toString())));
            queryCacheSize.setNormalNum(StringUtils.filterStr(jedis.get(EventLevel.NORMAL.toString())));
            queryCacheSize.setTotal(queryCacheSize.getEmergencyNum()
                    + queryCacheSize.getImportantNum() + queryCacheSize.getNormalNum());
            return queryCacheSize;
        } catch (IllegalStateException e) {
            LOGGER.error("query event size failed, sessionId:{}, error message:{}",
                    eventsRequestEntity.getSessionId(), e.getMessage());
            return queryCacheSize;
        }
    }

    /**
     * 获取事件field
     *
     * @param agentInstanceMeta agent实例
     * @param event 事件
     * @return 事件对应field
     */
    public String getEventField(InstanceMeta agentInstanceMeta, Event event) {
        String field = String.join(CommonConst.JOIN_REDIS_KEY,
                getField(agentInstanceMeta.getService()),
                getField(agentInstanceMeta.getNode().getIp()),
                getField(String.valueOf(event.getEventType().getDescription()).toLowerCase(Locale.ROOT)),
                getField(event.getEventLevel().toString().toLowerCase(Locale.ROOT)),
                getField(event.getScope()),
                getField(agentInstanceMeta.getInstanceId()),
                agentInstanceMeta.getCluster() != null ? getField(agentInstanceMeta.getCluster().getCluster()) : "",
                agentInstanceMeta.getEnvironment() != null ? getField(agentInstanceMeta.getEnvironment().getEnv()) : "",
                getField(agentInstanceMeta.getAz()),
                getField(event.getMetaHash()),
                getField(String.valueOf(event.getTime())));
        return field;
    }

    private String getField(String str) {
        return !StringUtils.isEmpty(str) ? str : "";
    }

    /**
     * 拼接查询条件
     *
     * @param event 查询条件
     * @return 事件查询模版
     */
    private String getPattern(EventsRequestEntity event) {
        List<String> patterns = new ArrayList<>();
        patterns.add(getListPattern(event.getService()));
        patterns.add(getListPattern(event.getIp()));
        patterns.add(getListPattern(event.getType() == null ? new ArrayList<>() : event.getType()));
        patterns.add(getListPattern(event.getLevel() == null ? new ArrayList<>() : event.getLevel()));
        patterns.add(getListPattern(event.getScope()));
        patterns.add(CommonConst.FULL_MATCH_KEY);
        return patterns.stream().map(String::valueOf).collect(Collectors.joining(CommonConst.JOIN_REDIS_KEY));
    }

    private String getListPattern(List<String> strings) {
        return strings.size() == 0 ? CommonConst.FULL_MATCH_KEY : "(" + String.join("|", strings) + ")";
    }

    /**
     * 定时任务，清理过期数据
     */
    public void cleanOverDueEventTimerTask() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -Integer.parseInt(backendConfig.getExpire()));
        List<String> needCleanEvent = queryByTimeRange(
                CommonConst.REDIS_EVENT_FIELD_SET_KEY, 0, calendar.getTimeInMillis());
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel(CommonConst.REDIS_EVENT_KEY, needCleanEvent.toArray(new String[0]));
            jedis.zrem(CommonConst.REDIS_EVENT_FIELD_SET_KEY, needCleanEvent.toArray(new String[0]));
            cleanOverDueEventLevel(jedis, needCleanEvent);
        } catch (IllegalStateException e) {
            LOGGER.error("delete over dur event failed, error message:{}", e.getMessage());
        }
    }

    /**
     * 删除过期事件同步设置事件级别数量
     *
     * @param jedis redis client
     * @param needCleanEvent 需要删除的事件key
     */
    private void cleanOverDueEventLevel(Jedis jedis, List<String> needCleanEvent) {
        for (String key : needCleanEvent) {
            EventLevel level = EventLevel.valueOf(
                    key.split(CommonConst.JOIN_REDIS_KEY)[EVENT_LEVEL_INDEX].toUpperCase(Locale.ROOT));
            switch (level) {
                case EMERGENCY:
                    jedis.decrBy(EventLevel.EMERGENCY.toString(), -1);
                    break;
                case IMPORTANT:
                    jedis.decrBy(EventLevel.IMPORTANT.toString(), -1);
                    break;
                case NORMAL:
                    jedis.decrBy(EventLevel.NORMAL.toString(), -1);
                    break;
                default:
                    break;
            }
        }
    }
}
