package networksimulator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RoutingController {
    private final Map<String, RouterNode> adjacencyList = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void addNode(String name) {
        rwLock.writeLock().lock();
        try {
            adjacencyList.putIfAbsent(name, new RouterNode(name));
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void addEdge(String source, String target, int latencyMs) {
        rwLock.writeLock().lock();
        try {
            addNode(source);
            addNode(target);
            adjacencyList.get(source).getEdges().add(new Edge(target, latencyMs));
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void updateNetworkLatency(String source, String target, int newLatency) {
        rwLock.writeLock().lock();
        try {
            if (adjacencyList.containsKey(source)) {
                for (Edge edge : adjacencyList.get(source).getEdges()) {
                    if (edge.getTarget().equals(target)) {
                        edge.setLatencyMs(newLatency);
                        System.out.println("[CHAOS CONTROL] Updated: " + source + " -> " + target + " to " + newLatency + "ms");
                        return;
                    }
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void updateNodeHealth(String name, boolean status) {
        rwLock.writeLock().lock();
        try {
            if (adjacencyList.containsKey(name)) {
                adjacencyList.get(name).setAlive(status);
                System.out.println("[CHAOS CONTROL] Health Status Shift: " + name + " isAlive = " + status);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public List<String> calculateShortestPath(String src, String dest) {
        rwLock.readLock().lock();
        try {
            if (!adjacencyList.containsKey(src) || !adjacencyList.containsKey(dest)) return null;
            if (!adjacencyList.get(src).isAlive() || !adjacencyList.get(dest).isAlive()) return null;

            if (!adjacencyList.get(src).getRateLimiter().allowRequest()) {
                System.out.println("[RATE LIMIT ACTIVE] Drop packet sequence at entry point: " + src);
                return null;
            }

            Map<String, Integer> distances = new HashMap<>();
            Map<String, String> parentMap = new HashMap<>();
            PriorityQueue<Edge> minHeap = new PriorityQueue<>(Comparator.comparingInt(Edge::getLatencyMs));

            for (String node : adjacencyList.keySet()) {
                distances.put(node, Integer.MAX_VALUE);
            }

            distances.put(src, 0);
            minHeap.add(new Edge(src, 0));

            while (!minHeap.isEmpty()) {
                Edge current = minHeap.poll();
                String u = current.getTarget();

                if (u.equals(dest)) break;

                RouterNode uNode = adjacencyList.get(u);
                if (uNode == null || !uNode.isAlive()) continue;

                for (Edge neighbor : uNode.getEdges()) {
                    RouterNode vNode = adjacencyList.get(neighbor.getTarget());
                    if (vNode == null || !vNode.isAlive()) continue;

                    int newDist = distances.get(u) + neighbor.getLatencyMs();
                    if (newDist < distances.get(neighbor.getTarget())) {
                        distances.put(neighbor.getTarget(), newDist);
                        parentMap.put(neighbor.getTarget(), u);
                        minHeap.add(new Edge(neighbor.getTarget(), newDist));
                    }
                }
            }

            if (distances.get(dest) == Integer.MAX_VALUE) return null;

            List<String> path = new ArrayList<>();
            String crawl = dest;
            while (crawl != null) {
                path.add(crawl);
                crawl = parentMap.get(crawl);
            }
            Collections.reverse(path);
            System.out.println("[VIEW GRAPH] Operational Path Matrix: " + path + " | Metric Cost: " + distances.get(dest) + "ms");
            return path;

        } finally {
            rwLock.readLock().unlock();
        }
    }
}