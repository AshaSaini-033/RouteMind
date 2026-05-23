package networksimulator;

public class Edge {
    private final String target;
    private int latencyMs;

    public Edge(String target, int latencyMs) {
        this.target = target;
        this.latencyMs = latencyMs;
    }

    public String getTarget() { return target; }
    public int getLatencyMs() { return latencyMs; }
    public void setLatencyMs(int latencyMs) { this.latencyMs = latencyMs; }
}