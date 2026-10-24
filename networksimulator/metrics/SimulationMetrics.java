package networksimulator.metrics;
import java.util.concurrent.atomic.LongAdder;

/** Thread-safe final-outcome counters for a simulation run. */
public final class SimulationMetrics {
    private final LongAdder submitted=new LongAdder(), delivered=new LongAdder(), rateLimited=new LongAdder(),
        noRoute=new LongAdder(), queueRejected=new LongAdder(), retries=new LongAdder(), failovers=new LongAdder();
    private final LatencyTracker latency=new LatencyTracker();
    public void submitted(){submitted.increment();} public void delivered(long nanos){delivered.increment();latency.recordNanos(nanos);}
    public void rateLimited(){rateLimited.increment();} public void noRoute(){noRoute.increment();}
    public void queueRejected(){queueRejected.increment();} public void retry(){retries.increment();}
    public void failover(){failovers.increment();}
    public long getSubmitted(){return submitted.sum();} public long getDelivered(){return delivered.sum();}
    public long getRateLimited(){return rateLimited.sum();} public long getNoRoute(){return noRoute.sum();}
    public long getQueueRejected(){return queueRejected.sum();} public long getRetries(){return retries.sum();}
    public long getFailovers(){return failovers.sum();} public LatencyTracker getLatency(){return latency;}
    public long getFailed(){return getRateLimited()+getNoRoute()+getQueueRejected();}
}