package org.nougat.arc.hexnet;

public class Packet {
    public final int payload;
    public final Address destination;

    public Packet(int payload, Address destination) {
        this.payload = payload;
        this.destination = destination;
    }
}
