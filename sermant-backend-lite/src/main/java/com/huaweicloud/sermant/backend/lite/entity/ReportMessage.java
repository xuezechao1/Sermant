package com.huaweicloud.sermant.backend.lite.entity;

/**
 * 类描述
 *
 * @author lilai
 * @since 2022-12-20
 */
public class ReportMessage {
    public ReportMessage(String eventType, String role, String serviceName,
                         String ipAddress, boolean isEnhance, String description,
                         String timeStamp, String instanceId, boolean zkOrGw,
                         String destination, boolean destinationWithSermant) {
        this.eventType = eventType;
        this.role = role;
        this.serviceName = serviceName;
        this.ipAddress = ipAddress;
        this.isEnhance = isEnhance;
        this.description = description;
        this.timeStamp = timeStamp;
        this.instanceId = instanceId;
        this.zkOrGw = zkOrGw;
        this.destination = destination;
        this.destinationWithSermant = destinationWithSermant;
    }

    public ReportMessage() {
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean getIsEnhance() {
        return isEnhance;
    }

    public void setIsEnhance(boolean isEnhance) {
        this.isEnhance = isEnhance;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setZkOrGw(boolean zkOrGw) {
        this.zkOrGw = zkOrGw;
    }

    public boolean getZkOrGw() {
        return zkOrGw;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestinationWithSermant(boolean destinationWithSermant) {
        this.destinationWithSermant = destinationWithSermant;
    }

    public boolean getDestinationWithSermant() {
        return destinationWithSermant;
    }

    private String eventType;
    private String role;
    private String serviceName;
    private String ipAddress;
    private boolean isEnhance;
    private String description;
    private String timeStamp;
    private String instanceId;
    private String destination;
    private boolean destinationWithSermant;
    private boolean zkOrGw;
}
