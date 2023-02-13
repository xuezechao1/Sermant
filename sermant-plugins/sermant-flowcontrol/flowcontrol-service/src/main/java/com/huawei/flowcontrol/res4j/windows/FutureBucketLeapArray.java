package com.huawei.flowcontrol.res4j.windows;

public class FutureBucketLeapArray extends LeapArray<MetricBucket> {
    public FutureBucketLeapArray(int sampleCount, int intervalInMs) {
        // This class is the original "BorrowBucketArray".
        super(sampleCount, intervalInMs);
    }

    @Override
    public MetricBucket newEmptyBucket(long time) {
        return new MetricBucket();
    }

    @Override
    protected WindowWrap<MetricBucket> resetWindowTo(WindowWrap<MetricBucket> w, long startTime) {
        // Update the start time and reset value.
        w.resetTo(startTime);
        w.value().reset();
        return w;
    }

    @Override
    public boolean isWindowDeprecated(long time, WindowWrap<MetricBucket> windowWrap) {
        // Tricky: will only calculate for future.
        return time >= windowWrap.windowStart();
    }
}
