package networksimulator.metrics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Thread-safe observed latency samples and percentile calculations. */
public final class LatencyTracker {
    private final ConcurrentLinkedQueue<Long> samples=new ConcurrentLinkedQueue<>();
    public void recordNanos(long nanos){samples.add(Math.max(0,nanos));}
    public int getSampleCount(){return samples.size();}
    public double averageMillis(){if(samples.isEmpty())return 0; long sum=0; for(long n:samples)sum+=n; return sum/(double)samples.size()/1_000_000.0;}
    public double percentileMillis(double p){
        if(p<0||p>100)throw new IllegalArgumentException("Percentile must be 0..100");
        var sorted=new ArrayList<>(samples); if(sorted.isEmpty())return 0; Collections.sort(sorted);
        int i=(int)Math.ceil(p/100.0*sorted.size())-1; return sorted.get(Math.max(0,Math.min(i,sorted.size()-1)))/1_000_000.0;
    }
}