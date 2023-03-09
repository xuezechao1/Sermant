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

package com.huaweicloud.sermant.backend.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 事件查询请求实体
 *
 * @since 2023-03-02
 * @author xuezechao
 */
@Getter
@Setter
public class EventsRequestEntity {

    private String application;

    private String ip;

    private String scope;

    private String type;

    private String level;

    private long startTime;

    private long endTime;
}