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

package com.huawei.sermant.backend.webhook.welink;

import com.huawei.sermant.backend.common.conf.CommonConst;
import com.huawei.sermant.backend.entity.EventEntity;
import com.huawei.sermant.backend.webhook.WebHookClient;
import com.huawei.sermant.backend.webhook.WebHookConfig;

import java.util.List;

/**
 * welink webhook 客户端
 *
 * @author xuezechao
 * @since 2023-03-02
 */
public class WelinkHookClient implements WebHookClient {

    private WebHookConfig welinkHookConfig = WelinkHookConfig.getInstance();

    /**
     * 构造函数
     */
    public WelinkHookClient() {
        welinkHookConfig.setName(CommonConst.WELINK_WEBHOOK_NAME);
    }

    /**
     * webhook 事件推送
     *
     * @param eventEntities 事件信息
     * @return
     */
    @Override
    public boolean doNotify(List<EventEntity> eventEntities) {
        return false;
    }

    /**
     * 获取配置
     *
     * @return 配置
     */
    @Override
    public WebHookConfig getConfig() {
        return welinkHookConfig;
    }
}
