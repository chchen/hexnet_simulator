package org.nougat.arc.hexnet;

import java.util.ArrayList;
import java.util.List;

public class Packet {
    public final int payload;
    public final Address source;
    public final Address destination;
    private final long sendTime;
    private List<Address> path;

    public Packet(int payload, Address source, Address destination) {
        this.payload = payload;
        this.source = source;
        this.destination = destination;
        this.sendTime = System.currentTimeMillis();
        path = new ArrayList<>();
    }

    public long latencyMs() {
        return System.currentTimeMillis()  - sendTime;
    }

    public void markPath(Address address) {
        path.add(address);
    }

    public List<Address> getPath() {
        return path;
    }

    public String getPathString() {
        StringBuilder pathStr = new StringBuilder();
        for (Address a : path) {
            pathStr.append(String.format("%s→", a.asString()));
        }
        pathStr.append("∎");
        return pathStr.toString();
    }
}
