/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.sermant.backend.cache;

import com.huawei.sermant.backend.entity.HeartbeatEntity;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

public class DeleteTimeoutData extends TimerTask {

    private static final int MAX_EFFECTIVE_TIME = 3000;

    /**
     * 初始化任务
     */
    public DeleteTimeoutData() {
        deleteHeartbeatCache();
    }

    @Override
    public void run() {
        deleteHeartbeatCache();
    }

    private void deleteHeartbeatCache() {
        Map<String, HeartbeatEntity> heartbeatMessages = HeartbeatCache.getHeartbeatMessages();
        for (Iterator<Map.Entry<String, HeartbeatEntity>> it = heartbeatMessages.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, HeartbeatEntity> heartbeatEntityEntry = it.next();
            long nowTime = System.currentTimeMillis();
            long lastHeartbeatTime = heartbeatEntityEntry.getValue().getLastHeartbeat();
            if ((nowTime - lastHeartbeatTime) > MAX_EFFECTIVE_TIME) {
                it.remove();
            }
        }
    }
}
