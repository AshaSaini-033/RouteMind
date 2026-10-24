package networksimulator.simulation;
import java.util.UUID;

/** One logical request; retries must not create another logical request. */
public final class Packet {
    private final String id=UUID.randomUUID().toString().substring(0,8);
    private final String source, destination;
    private final int payloadKb;
    private final long createdNanos=System.nanoTime();
    public Packet(String source,String destination,int payloadKb){this.source=source;this.destination=destination;this.payloadKb=payloadKb;}
    public String getId(){return id;} public String getSource(){return source;}
    public String getDestination(){return destination;} public int getPayloadKb(){return payloadKb;}
    public long getCreatedNanos(){return createdNanos;}
}