package networksimulator.reliability;

/** Computes capped exponential-backoff delays. The caller controls retry execution. */
public final class RetryPolicy {
    private final int maxRetries; private final long initialDelayMillis,maxDelayMillis;
    public RetryPolicy(int maxRetries,long initialDelayMillis,long maxDelayMillis){
        if(maxRetries<0||initialDelayMillis<0||maxDelayMillis<initialDelayMillis)throw new IllegalArgumentException("Invalid retry configuration");
        this.maxRetries=maxRetries;this.initialDelayMillis=initialDelayMillis;this.maxDelayMillis=maxDelayMillis;
    }
    public int getMaxRetries(){return maxRetries;}
    public long delayMillisForRetry(int retryNumber){
        if(retryNumber<1)throw new IllegalArgumentException("Retry number starts at 1");
        long delay=initialDelayMillis;
        for(int i=1;i<retryNumber&&delay<maxDelayMillis;i++)delay=Math.min(maxDelayMillis,delay>Long.MAX_VALUE/2?maxDelayMillis:delay*2);
        return Math.min(delay,maxDelayMillis);
    }
}