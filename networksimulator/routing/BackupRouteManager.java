package networksimulator.routing;
import java.util.List;
import networksimulator.RoutingController;

/** Recalculates a route against the current topology after a path failure. */
public final class BackupRouteManager {
    private final RoutingStrategy strategy;
    public BackupRouteManager(RoutingStrategy strategy){this.strategy=strategy;}
    public List<String> findCurrentRoute(RoutingController controller,String source,String destination){
        return strategy.findRoute(controller,source,destination);
    }
}