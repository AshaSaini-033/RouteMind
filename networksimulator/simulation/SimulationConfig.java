package networksimulator.simulation;

/** Immutable input for one CLI-driven traffic simulation. */
public final class SimulationConfig {
    private final String source, destination;
    private final int requestCount, requestsPerSecond, packetSizeKb;
    private final Integer latencyOverrideMs;

    public SimulationConfig(String source, String destination, int requestCount,
                           int requestsPerSecond, int packetSizeKb, Integer latencyOverrideMs) {
        if (source == null || source.isBlank() || destination == null || destination.isBlank())
            throw new IllegalArgumentException("Source and destination are required.");
        if (requestCount < 1 || requestCount > 1_000_000) throw new IllegalArgumentException("Requests must be 1..1,000,000.");
        if (requestsPerSecond < 1 || requestsPerSecond > 100_000) throw new IllegalArgumentException("Rate must be 1..100,000 requests/sec.");
        if (packetSizeKb < 1 || packetSizeKb > 65_536) throw new IllegalArgumentException("Packet size must be 1..65,536 KB.");
        if (latencyOverrideMs != null && latencyOverrideMs < 0) throw new IllegalArgumentException("Latency cannot be negative.");
        this.source=source.trim(); this.destination=destination.trim(); this.requestCount=requestCount;
        this.requestsPerSecond=requestsPerSecond; this.packetSizeKb=packetSizeKb; this.latencyOverrideMs=latencyOverrideMs;
    }
    public String getSource(){return source;} public String getDestination(){return destination;}
    public int getRequestCount(){return requestCount;} public int getRequestsPerSecond(){return requestsPerSecond;}
    public int getPacketSizeKb(){return packetSizeKb;} public Integer getLatencyOverrideMs(){return latencyOverrideMs;}
}