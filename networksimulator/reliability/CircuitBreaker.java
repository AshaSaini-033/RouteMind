package networksimulator.reliability;

/** Thread-safe circuit-breaker state machine for a repeatedly failing dependency. */
public final class CircuitBreaker {
    public enum State { CLOSED, OPEN, HALF_OPEN }
    private final int threshold;
    private final long openNanos;
    private int failures;
    private long openedAt;
    private State state=State.CLOSED;
    private boolean halfOpenProbeInFlight;

    public CircuitBreaker(int threshold,long openDurationMillis){
        if(threshold<1||openDurationMillis<1)throw new IllegalArgumentException("Threshold and duration must be positive");
        this.threshold=threshold; this.openNanos=openDurationMillis*1_000_000L;
    }
    public synchronized boolean allowRequest(){
        if(state==State.CLOSED)return true;
        if(state==State.OPEN&&System.nanoTime()-openedAt>=openNanos){state=State.HALF_OPEN;halfOpenProbeInFlight=false;}
        if(state==State.HALF_OPEN&&!halfOpenProbeInFlight){halfOpenProbeInFlight=true;return true;}
        return false;
    }
    public synchronized void recordSuccess(){failures=0;state=State.CLOSED;halfOpenProbeInFlight=false;}
    public synchronized void recordFailure(){
        failures++; halfOpenProbeInFlight=false;
        if(state==State.HALF_OPEN||failures>=threshold){state=State.OPEN;openedAt=System.nanoTime();}
    }
    public synchronized State getState(){return state;}
}