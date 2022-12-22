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

package com.huawei.discovery.interceptors;

import com.huawei.discovery.entity.RegisterContext;
import com.huawei.discovery.service.RegistryService;

import com.huawei.discovery.service.ReportService;
import com.huaweicloud.sermant.core.common.BootArgsIndexer;
import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.core.plugin.service.PluginServiceManager;

import com.huaweicloud.sermant.core.service.ServiceManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring关闭事件监听{@link org.springframework.context.event.ContextClosedEvent}
 *
 * @author zhouss
 * @since 2022-11-16
 */
public class SpringCloseEventInterceptor extends AbstractInterceptor {
    private final RegistryService registryService;

    /**
     * 构造器
     */
    public SpringCloseEventInterceptor() {
        registryService = PluginServiceManager.getPluginService(RegistryService.class);
    }

    @Override
    public ExecuteContext before(ExecuteContext context) throws Exception {
        final Object rawEvent = context.getArguments()[0];
        if (rawEvent instanceof ContextClosedEvent) {
            tryShutdown((ContextClosedEvent) rawEvent);
        }
        return context;
    }

    private void tryShutdown(ContextClosedEvent event) throws UnknownHostException {
        if (event.getSource() instanceof AnnotationConfigApplicationContext) {
            // 该类型属于刷新事件, 不予以处理
            return;
        }
        registryService.shutdown();
        ReportService reportService = ServiceManager.getService(ReportService.class);
        Map<String, Object> params = new HashMap<>();
        params.put("eventType", "unRegister");
        params.put("role", BootArgsIndexer.getAppName());
        params.put("serviceName", RegisterContext.INSTANCE.getServiceInstance().getServiceName());
        params.put("ipAddress", InetAddress.getLocalHost().getHostAddress());
        params.put("isEnhance", true);
        params.put("description", "[devspore] service unRegistry");
        params.put("timeStamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        params.put("instanceId", BootArgsIndexer.getInstanceId());
        reportService.report(params);
    }

    @Override
    public ExecuteContext after(ExecuteContext context) throws Exception {
        return context;
    }
}
