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

package com.huawei.sermant.backend.common.util;

/**
 * 字符串工具类
 *
 * @author xuezechao
 * @since 2023-03-02
 */
public class StringUtils {

    /**
     * 构造函数
     */
    private StringUtils() {

    }

    /**
     * 字符串判空
     *
     * @param val 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String val) {
        return val == null || "".equals(val.trim());
    }
}
