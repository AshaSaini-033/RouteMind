package networksimulator.routing;
import java.util.List;
import networksimulator.RoutingController;

/** Adapter around RouteMind's existing Dijkstra implementation. */
public final class DijkstraRouter implements RoutingStrategy {
    public List<String> findRoute(RoutingController controller,String source,String destination) {
        return controller.calculateShortestPath(source,destination);
    }
    public String name(){return "Dijkstra (minimum latency)";}
}