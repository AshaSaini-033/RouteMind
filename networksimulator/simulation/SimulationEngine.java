package networksimulator.simulation;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import networksimulator.RoutingController;
import networksimulator.metrics.SimulationMetrics;
import networksimulator.routing.BackupRouteManager;
import networksimulator.routing.DijkstraRouter;

/** CLI-driven concurrent traffic simulator over an in-memory topology, not real sockets. */
public final class SimulationEngine implements AutoCloseable {
    private final RoutingController controller;
    private final SimulationConfig config;
    private final SimulationMetrics metrics=new SimulationMetrics();
    private final AtomicBoolean stopped=new AtomicBoolean();
    private final AtomicLong completed=new AtomicLong();
    private final int workers,queueCapacity;
    private ThreadPoolExecutor executor;

    public SimulationEngine(RoutingController controller,SimulationConfig config){this(controller,config,Math.max(2,Runtime.getRuntime().availableProcessors()),256);}
    public SimulationEngine(RoutingController controller,SimulationConfig config,int workers,int queueCapacity){
        if(workers<1||queueCapacity<1)throw new IllegalArgumentException("Workers and queue capacity must be positive");
        this.controller=controller;this.config=config;this.workers=workers;this.queueCapacity=queueCapacity;
    }
    public SimulationMetrics run() throws InterruptedException {
        if(executor!=null)throw new IllegalStateException("Engine instances can run only once");
        executor=new ThreadPoolExecutor(workers,workers,0L,TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.AbortPolicy());
        TrafficGenerator generator=new TrafficGenerator(config);
        try {
            generator.generate(packet->{
                if(stopped.get())return;
                metrics.submitted();
                try { executor.execute(()->process(packet)); }
                catch(java.util.concurrent.RejectedExecutionException full){metrics.queueRejected();completed.incrementAndGet();}
            });
        } finally {
            executor.shutdown();
            if(!executor.awaitTermination(5,TimeUnit.MINUTES)){executor.shutdownNow();executor.awaitTermination(5,TimeUnit.SECONDS);}
        }
        return metrics;
    }
    private void process(Packet packet){
        long start=System.nanoTime();
        try {
            var path=new BackupRouteManager(new DijkstraRouter()).findCurrentRoute(controller,packet.getSource(),packet.getDestination());
            if(path==null||path.isEmpty()) {
                if(controller.wasLastRequestRateLimited()) metrics.rateLimited();
                else metrics.noRoute();
            } else {
                metrics.delivered(System.nanoTime()-start);
            }
        } catch(RuntimeException error){metrics.noRoute();}
        finally{completed.incrementAndGet();}
    }
    public long getCompleted(){return completed.get();}
    public SimulationMetrics getMetrics(){return metrics;}
    public boolean isStopped(){return stopped.get();}
    public void stop(){stopped.set(true);if(executor!=null)executor.shutdownNow();}
    public void close(){stop();}
}