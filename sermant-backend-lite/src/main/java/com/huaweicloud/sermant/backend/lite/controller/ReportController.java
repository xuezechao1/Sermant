package com.huaweicloud.sermant.backend.lite.controller;

import com.huaweicloud.sermant.backend.lite.entity.ReportMessage;
import com.huaweicloud.sermant.backend.lite.entity.ServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 类描述
 *
 * @author lilai
 * @since 2022-12-20
 */
@RestController
@RequestMapping
public class ReportController {

    private static final String UPDATE_CONFIG = "updateConfig";
    private static final String REGISTER = "register";
    private static final String UNREGISTER = "unRegister";
    private static final String REQUEST = "request";
    private static final String ZK_OR_GW = "zkOrGw";
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);
    private static final BlockingQueue<ReportMessage> queue = new ArrayBlockingQueue<>(1000);
    private static final HashMap<String, Object> serviceInfoMap = new HashMap<>();

    @PostMapping("/report")
    public void saveMessageInfo(@RequestBody ReportMessage reportMessage) throws InterruptedException {
        updateServiceInfo(reportMessage);
        boolean offerResult = queue.offer(reportMessage);
        if(offerResult) {
            LOGGER.info("put reportMessage to queue success!");
        } else {
            LOGGER.info("put reportMessage to queue fail!");
        }
    }

    @GetMapping("/getMessage")
    public ReportMessage getMessageInfo() {
        LOGGER.info("get reportMessage from queue");
        return queue.poll();
    }

    @GetMapping("/getInstanceStatus")
    public HashMap<String, Object> getIpStatus() {
        return serviceInfoMap;
    }

    private void updateServiceInfo(ReportMessage reportMessage) {
        if (reportMessage.getEventType().equals(UPDATE_CONFIG)) {
            serviceInfoMap.put(ZK_OR_GW, reportMessage.getZkOrGw());
        } else {
            ServiceInfo serviceInfo = new ServiceInfo();
            if (reportMessage.getEventType().equals(REGISTER) || reportMessage.getEventType().equals(REQUEST)) {
                serviceInfo.setStatus(true);
            }
            if (reportMessage.getEventType().equals(UNREGISTER)) {
                serviceInfo.setStatus(false);
            }
            serviceInfo.setIpAddress(reportMessage.getIpAddress());
            serviceInfo.setServiceName(reportMessage.getServiceName());
            serviceInfo.setInstanceId(reportMessage.getInstanceId());
            serviceInfoMap.put(reportMessage.getRole(), serviceInfo);
        }
    }
}
