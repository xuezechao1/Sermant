/*
 * Copyright (C) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved
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

package com.huawei.route.common.gray.strategy.match;

import com.huawei.route.common.gray.strategy.ValueMatchStrategy;

import java.util.List;

/**
 * 大于匹配策略
 *
 * @author pengyuyi
 * @date 2021/10/23
 */
public class GreaterValueMatchStrategy implements ValueMatchStrategy {
    @Override
    public boolean isMatch(List<String> values, String arg) {
        try {
            return Integer.parseInt(arg) > Integer.parseInt(values.get(0));
        } catch (Exception e) {
            return false;
        }
    }
}