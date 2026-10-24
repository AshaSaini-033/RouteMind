package networksimulator.simulation;
import java.util.concurrent.atomic.AtomicBoolean;

/** Rate-controlled traffic producer using monotonic time. */
public final class TrafficGenerator {
    private final SimulationConfig config; private final AtomicBoolean cancelled=new AtomicBoolean();
    public TrafficGenerator(SimulationConfig config){this.config=config;}
    public void cancel(){cancelled.set(true);} public boolean isCancelled(){return cancelled.get();}
    public void generate(PacketConsumer consumer)throws InterruptedException{
        long interval=Math.max(1L,1_000_000_000L/config.getRequestsPerSecond()), next=System.nanoTime();
        for(int i=0;i<config.getRequestCount()&&!cancelled.get();i++){
            long wait;
            while((wait=next-System.nanoTime())>0&&!cancelled.get())Thread.sleep(wait/1_000_000L,(int)(wait%1_000_000L));
            if(cancelled.get())break;
            consumer.accept(new Packet(config.getSource(),config.getDestination(),config.getPacketSizeKb()));
            next+=interval;
        }
    }
    @FunctionalInterface public interface PacketConsumer{void accept(Packet packet)throws InterruptedException;}
}