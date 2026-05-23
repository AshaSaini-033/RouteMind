# High-Performance Concurrent Network Routing & Topography Simulator

A production-grade, multi-threaded Command Line Interface (CLI) infrastructure engine designed to simulate real-world dynamic network routing, load-balancing, and fault tolerance. Built entirely from scratch using Java, this project focuses heavily on Core Data Structures, Advanced Graph Algorithms, and low-level System Design principles without relying on high-level frameworks or UIs.

---

## 🚀 Key Features & Problems Solved

### 1. Dynamic Path Optimization (DSA Core)
* **Problem:** Finding the fastest execution path for packets in a fluctuating network topography.
* **Solution:** Modeled the network as a sparse **Adjacency List Graph** to optimize space complexity to $O(V + E)$. Implemented **Dijkstra’s Shortest Path Algorithm** utilizing a custom binary **Min-Heap (Priority Queue)** to resolve routes with the lowest latency overhead in $O((E + V) \log V)$ time complexity.

### 2. Thread-Safe State Mutation (Concurrency & System Design)
* **Problem:** Race conditions occurring when network engineers modify latency metrics or nodes crash while packet transport requests are reading the topology graph.
* **Solution:** Implemented structural concurrency boundaries using a **ReentrantReadWriteLock**. This allows concurrent read operations (`ReadLock`) for infinite simultaneous pathfinding requests while ensuring exclusive synchronization locks (`WriteLock`) during unexpected runtime configuration updates or telemetry mutations.

### 3. Fault Tolerance & Self-Healing Routing
* **Problem:** Hardware infrastructure breakdowns or cut fiber lines shouldn't trigger total data dropouts across a live network stream.
* **Solution:** Engineered a robust failover logic where individual vertex entities can be dynamically marked offline. The core pathfinder dynamically catches unreachable states and automatically switches to secondary and tertiary backup detours mid-transit without dropping user sessions.

### 4. DDoS Mitigation via Token Bucket Rate Limiting
* **Problem:** Malicious buffer overflows or resource starvation at an ingestion gateway router can knock nodes offline.
* **Solution:** Protected every network router node interface with a dedicated, thread-safe thread-level **Token Bucket Rate Limiter**. High-frequency traffic spikes beyond configured capacities are automatically dropped (`429 Too Many Requests` equivalent) to protect down-stream structural nodes.

---

## 🛠️ System Architecture & Data Flow

```text
       [ LAYER 1: USER INTERACTIVE CLI ]
          (Scanner Commands / Operations)
                       │
                       ▼
       [ LAYER 2: THE CONCURRENT ROUTING CONTROLLER ]
   ┌──────────────────────────────────────────────────┐
   │  • Topology Map: Thread-Safe Adjacency List     │
   │  • Protection: Token Bucket Rate Limiting Engine │
   │  • Optimization Engine: Dij





kstra's Pathfinder   │
   └──────────────────────────────────────────────────┘
                       │
                       ▼
       [ LAYER 3: DYNAMIC TOPOLOGY INFRASTRUCTURE ]
          ┌───────────────┐           ┌───────────────┐
          │ Node A (Delhi)│───────────│Node B (Mumbai)│
          └───────────────┘           └───────────────┘
                  │                           │
                  └─────────────┬─────────────┘
                                ▼
                        ┌───────────────┐
                        │Node C (Blr)   │
                        └───────────────┘




src/networksimulator/
├── Edge.java               # Model: Defines directional point relations and runtime latency weights
├── TokenBucket.java        # Model Component: Thread-safe token tracking component for traffic control
├── RouterNode.java         # Model: Encapsulates vertex operational attributes, links, and health stats
├── RoutingController.java  # Controller: Manages global locks, path calculation, and topology updates
└── NetworkConsoleView.java # View/Driver: The interactive terminal console scanner execution loop


Enter Command: ROUTE Delhi Bengaluru
[VIEW GRAPH] Operational Path Matrix: [Delhi, Mumbai, Bengaluru] | Metric Cost: 50ms

Enter Command: LATENCY Delhi Mumbai 300
[CHAOS CONTROL] Updated: Delhi -> Mumbai to 300ms

Enter Command: ROUTE Delhi Bengaluru
[VIEW GRAPH] Operational Path Matrix: [Delhi, Hyderabad, Bengaluru] | Metric Cost: 65ms

Enter Command: CRASH Hyderabad
[CHAOS CONTROL] Health Status Shift: Hyderabad isAlive = false

Enter Command: ROUTE Delhi Bengaluru
[VIEW GRAPH] Operational Path Matrix: [Delhi, Bengaluru] | Metric Cost: 100ms

[RATE LIMIT ACTIVE] Drop packet sequence at entry point: Delhi



