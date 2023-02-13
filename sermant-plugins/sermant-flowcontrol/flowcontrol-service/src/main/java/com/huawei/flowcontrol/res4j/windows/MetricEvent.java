package com.huawei.flowcontrol.res4j.windows;

public enum MetricEvent {
    /**
     * Normal pass.
     */
    PASS,
    /**
     * Normal block.
     */
    BLOCK,
    EXCEPTION,
    SUCCESS,
    RT,

    /**
     * Passed in future quota (pre-occupied, since 1.5.0).
     */
    OCCUPIED_PASS
}
