package networksimulator.routing;
import java.util.List;
import networksimulator.RoutingController;

/** Strategy pattern: lets the simulator swap routing algorithms later. */
public interface RoutingStrategy {
    List<String> findRoute(RoutingController controller,String source,String destination);
    String name();
}