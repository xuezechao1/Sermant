package com.huawei.flowcontrol.res4j.windows;

import java.util.List;

public class OccupiableBucketLeapArray extends LeapArray<MetricBucket> {
    private final FutureBucketLeapArray borrowArray;

    public OccupiableBucketLeapArray(int sampleCount, int intervalInMs) {
        // This class is the original "CombinedBucketArray".
        super(sampleCount, intervalInMs);
        this.borrowArray = new FutureBucketLeapArray(sampleCount, intervalInMs);
    }

    @Override
    public MetricBucket newEmptyBucket(long time) {
        MetricBucket newBucket = new MetricBucket();

        MetricBucket borrowBucket = borrowArray.getWindowValue(time);
        if (borrowBucket != null) {
            newBucket.reset(borrowBucket);
        }

        return newBucket;
    }

    @Override
    protected WindowWrap<MetricBucket> resetWindowTo(WindowWrap<MetricBucket> w, long time) {
        // Update the start time and reset value.
        w.resetTo(time);
        MetricBucket borrowBucket = borrowArray.getWindowValue(time);
        if (borrowBucket != null) {
            w.value().reset();
            w.value().addPass((int)borrowBucket.pass());
        } else {
            w.value().reset();
        }

        return w;
    }

    @Override
    public long currentWaiting() {
        borrowArray.currentWindow();
        long currentWaiting = 0;
        List<MetricBucket> list = borrowArray.values();

        for (MetricBucket window : list) {
            currentWaiting += window.pass();
        }
        return currentWaiting;
    }

    @Override
    public void addWaiting(long time, int acquireCount) {
        WindowWrap<MetricBucket> window = borrowArray.currentWindow(time);
        window.value().add(MetricEvent.PASS, acquireCount);
    }

    @Override
    public void debug(long time) {
        StringBuilder sb = new StringBuilder();
        List<WindowWrap<MetricBucket>> lists = listAll();
        sb.append("a_Thread_").append(Thread.currentThread().getId()).append(" time=").append(time).append("; ");
        for (WindowWrap<MetricBucket> window : lists) {
            sb.append(window.windowStart()).append(":").append(window.value().toString()).append(";");
        }
        sb.append("\n");

        lists = borrowArray.listAll();
        sb.append("b_Thread_").append(Thread.currentThread().getId()).append(" time=").append(time).append("; ");
        for (WindowWrap<MetricBucket> window : lists) {
            sb.append(window.windowStart()).append(":").append(window.value().toString()).append(";");
        }
        System.out.println(sb.toString());
    }
}
