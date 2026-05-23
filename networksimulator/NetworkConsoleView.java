package networksimulator;

import java.util.Scanner;

public class NetworkConsoleView {
    public static void main(String[] args) {
        RoutingController controller = new RoutingController();

        // 1. Base Map Setup (Delhi, Mumbai, Bengaluru, Hyderabad)
        controller.addEdge("Delhi", "Mumbai", 30);
        controller.addEdge("Mumbai", "Bengaluru", 20);
        controller.addEdge("Delhi", "Bengaluru", 100); 
        controller.addEdge("Delhi", "Hyderabad", 40);
        controller.addEdge("Hyderabad", "Bengaluru", 25);

        Scanner scanner = new Scanner(System.in);
        System.out.println("=========================================");
        System.out.println("    INTERACTIVE ROUTING CLI INITIALIZED   ");
        System.out.println("=========================================");
        System.out.println("Commands you can type:");
        System.out.println("1. ROUTE [src] [dest]           -> Example: ROUTE Delhi Bengaluru");
        System.out.println("2. LATENCY [src] [dest] [ms]    -> Example: LATENCY Delhi Mumbai 300");
        System.out.println("3. CRASH [node_name]            -> Example: CRASH Hyderabad");
        System.out.println("4. EXIT                         -> To stop program\n");

        while (true) {
            System.out.print("\nEnter Command: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            
            String[] parts = input.split(" ");
            String command = parts[0].toUpperCase();

            if (command.equals("EXIT")) break;

            switch (command) {
                case "ROUTE":
                    if (parts.length == 3) {
                        controller.calculateShortestPath(parts[1], parts[2]);
                    } else {
                        System.out.println("Error: Use format 'ROUTE Delhi Bengaluru'");
                    }
                    break;

                case "LATENCY":
                    if (parts.length == 4) {
                        controller.updateNetworkLatency(parts[1], parts[2], Integer.parseInt(parts[3]));
                    } else {
                        System.out.println("Error: Use format 'LATENCY Delhi Mumbai 300'");
                    }
                    break;

                case "CRASH":
                    if (parts.length == 2) {
                        controller.updateNodeHealth(parts[1], false);
                    } else {
                        System.out.println("Error: Use format 'CRASH Hyderabad'");
                    }
                    break;

                default:
                    System.out.println("Unknown Command! Try ROUTE, LATENCY, or CRASH.");
            }
        }
        scanner.close();
        System.out.println("=== Engine Stopped ===");
    }
}