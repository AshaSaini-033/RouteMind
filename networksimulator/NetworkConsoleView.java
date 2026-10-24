package networksimulator;

import java.util.Scanner;
import networksimulator.metrics.SimulationMetrics;
import networksimulator.simulation.SimulationConfig;
import networksimulator.simulation.SimulationEngine;

public class NetworkConsoleView {
    public static void main(String[] args) {
        RoutingController controller = new RoutingController();
        controller.addEdge("Delhi", "Mumbai", 30);
        controller.addEdge("Mumbai", "Bengaluru", 20);
        controller.addEdge("Delhi", "Bengaluru", 100);
        controller.addEdge("Delhi", "Hyderabad", 40);
        controller.addEdge("Hyderabad", "Bengaluru", 25);

        try (Scanner scanner = new Scanner(System.in)) {
            printHelp();
            while (true) {
                System.out.print("\nRouteMind> ");
                if (!scanner.hasNextLine()) break;
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;
                String[] parts = input.split("\\s+");
                String command = parts[0].toUpperCase();

                try {
                    switch (command) {
                        case "EXIT":
                        case "QUIT":
                            System.out.println("RouteMind stopped.");
                            return;
                        case "HELP":
                            printHelp();
                            break;
                        case "ROUTE":
                            if (parts.length != 3) {
                                System.out.println("Usage: ROUTE <source> <destination>");
                            } else {
                                controller.calculateShortestPath(parts[1], parts[2]);
                            }
                            break;
                        case "LATENCY":
                            if (parts.length != 4) {
                                System.out.println("Usage: LATENCY <source> <target> <milliseconds>");
                            } else {
                                int latency = Integer.parseInt(parts[3]);
                                if (latency < 0) throw new IllegalArgumentException("Latency cannot be negative.");
                                controller.updateNetworkLatency(parts[1], parts[2], latency);
                            }
                            break;
                        case "CRASH":
                            if (parts.length != 2) System.out.println("Usage: CRASH <node>");
                            else controller.updateNodeHealth(parts[1], false);
                            break;
                        case "RECOVER":
                            if (parts.length != 2) System.out.println("Usage: RECOVER <node>");
                            else controller.updateNodeHealth(parts[1], true);
                            break;
                        case "SIMULATE":
                            runSimulation(scanner, controller);
                            break;
                        default:
                            System.out.println("Unknown command. Type HELP.");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter a valid integer.");
                } catch (IllegalArgumentException ex) {
                    System.out.println("Invalid input: " + ex.getMessage());
                }
            }
        }
    }

    private static void runSimulation(Scanner scanner, RoutingController controller) {
        System.out.println("\n--- Configure traffic simulation ---");
        String source = ask(scanner, "Source node (e.g. Delhi): ");
        String destination = ask(scanner, "Destination node (e.g. Bengaluru): ");
        int count = readInt(scanner, "Number of requests (1..1000000): ", 1, 1_000_000);
        int rate = readInt(scanner, "Target request rate/sec (1..100000): ", 1, 100_000);
        int payload = readInt(scanner, "Payload size KB (1..65536): ", 1, 65_536);

        SimulationConfig config = new SimulationConfig(source, destination, count, rate, payload, null);
        System.out.printf("\nStarting %d logical requests from %s to %s at target %d req/sec...%n",
                count, source, destination, rate);
        long started = System.nanoTime();
        try (SimulationEngine engine = new SimulationEngine(controller, config)) {
            SimulationMetrics metrics = engine.run();
            long elapsedNanos = System.nanoTime() - started;
            printReport(metrics, elapsedNanos);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.out.println("Simulation interrupted; shutdown requested.");
        }
    }

    private static String ask(Scanner scanner, String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) throw new IllegalStateException("Input ended.");
        String value = scanner.nextLine().trim();
        if (value.isEmpty()) throw new IllegalArgumentException("Value cannot be empty.");
        return value;
    }

    private static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            String raw = ask(scanner, prompt);
            try {
                int value = Integer.parseInt(raw);
                if (value < min || value > max) {
                    System.out.printf("Enter a value between %d and %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Enter a valid integer.");
            }
        }
    }

    private static void printReport(SimulationMetrics m, long elapsedNanos) {
        long total = m.getSubmitted();
        long delivered = m.getDelivered();
        long rejected = m.getQueueRejected();
        long noRoute = m.getNoRoute();
        long classified = delivered + rejected + noRoute + m.getRateLimited();
        double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
        System.out.println("\n========== ROUTEMIND SIMULATION REPORT ==========");
        System.out.printf("Logical requests submitted : %d%n", total);
        System.out.printf("Route found / accepted     : %d%n", delivered);
        System.out.printf("No route / route rejected  : %d%n", noRoute);
        System.out.printf("Rate-limited (unclassified): %d%n", m.getRateLimited());
        System.out.printf("Queue rejected             : %d%n", rejected);
        System.out.printf("Retries                    : %d%n", m.getRetries());
        System.out.printf("Failovers                  : %d%n", m.getFailovers());
        System.out.printf("Observed avg route time    : %.3f ms%n", m.getLatency().averageMillis());
        System.out.printf("Observed p95 route time    : %.3f ms%n", m.getLatency().percentileMillis(95));
        System.out.printf("Simulation wall time       : %.3f sec%n", elapsedSeconds);
        System.out.printf("Submission throughput      : %.2f req/sec%n",
                elapsedSeconds > 0 ? total / elapsedSeconds : 0.0);
        System.out.printf("Accounted final outcomes   : %d%n", classified);
        System.out.println("NOTE: latency is local route-computation time, not real network transit latency.");
        System.out.println("NOTE: current controller API conflates rate-limit and no-route outcomes.");
        System.out.println("================================================");
    }

    private static void printHelp() {
        System.out.println("\n==============================================");
        System.out.println("       ROUTEMIND NETWORK ROUTING CLI");
        System.out.println("==============================================");
        System.out.println("SIMULATE             Configure and run concurrent traffic");
        System.out.println("ROUTE <src> <dest>   Find a minimum-latency path");
        System.out.println("LATENCY <src> <dest> <ms>  Change a directed link latency");
        System.out.println("CRASH <node>         Mark a router unavailable");
        System.out.println("RECOVER <node>       Restore a router");
        System.out.println("STATUS               (planned) Show network health");
        System.out.println("STATS                (planned) Show last simulation metrics");
        System.out.println("HELP                 Show commands");
        System.out.println("EXIT                 Stop CLI");
        System.out.println("==============================================");
    }
}