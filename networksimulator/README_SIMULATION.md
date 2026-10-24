# RouteMind traffic simulation

This branch introduces a first integration layer for CLI-driven traffic simulation while preserving the existing CLI and Dijkstra implementation.

## Added components
- `SimulationConfig`: validates source, destination, request count, request rate, payload size and optional latency override.
- `Packet`: unique ID and metadata for one logical request.
- `TrafficGenerator`: rate-controlled producer using monotonic time.
- `SimulationEngine`: bounded concurrent worker pool and overload rejection.
- `RoutingStrategy` / `DijkstraRouter`: Strategy-pattern seam around the current routing algorithm.
- `BackupRouteManager`: recalculates a route against current topology.
- `CircuitBreaker`: thread-safe CLOSED/OPEN/HALF_OPEN state machine.
- `RetryPolicy`: capped exponential-backoff calculation.
- `SimulationMetrics` / `LatencyTracker`: thread-safe counters and observed route-processing latency.

## Important limitations (do not overclaim)
- The simulation runs over the in-memory graph; it does not send real network packets.
- The current `RoutingController.calculateShortestPath` API returns null both when rate-limited and when no route exists. The current engine therefore reports null outcomes as route failures. A typed result API is needed to split these correctly.
- The optional latency override is validated/stored but is not yet applied to a specific edge.
- Latency values currently represent local route-computation time, not simulated network transit latency.
- CircuitBreaker and RetryPolicy are primitives, not yet wired into request processing.
- Automatic link failure injection, health probes and full failover orchestration are not yet integrated.

## Compile
The current repository has Java sources under `networksimulator/` at the root. Use Java 17+:
```bash
javac $(find networksimulator -name '*.java')
```

Run the existing CLI:
```bash
java networksimulator.NetworkConsoleView
```

The interactive prompts and final report are the next integration step. This branch intentionally separates new components from the existing CLI until the routing API can return typed outcomes.
