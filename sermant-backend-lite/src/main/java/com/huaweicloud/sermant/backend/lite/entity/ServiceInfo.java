package com.huaweicloud.sermant.backend.lite.entity;

import lombok.Data;

@Data
public class ServiceInfo {
    Boolean status;
    String ipAddress;
    String serviceName;
    String instanceId;
}
