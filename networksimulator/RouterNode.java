package networksimulator;

import java.util.ArrayList;
import java.util.List;

public class RouterNode {
    private final String name;
    private final List<Edge> edges;
    private final TokenBucket rateLimiter;
    private boolean isAlive;

    public RouterNode(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
        this.rateLimiter = new TokenBucket(5, 2); 
        this.isAlive = true;
    }

    public String getName() { return name; }
    public List<Edge> getEdges() { return edges; }
    public TokenBucket getRateLimiter() { return rateLimiter; }
    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }
}