package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.NorthIn;
import org.nougat.arc.hexnet.Packet;
import org.nougat.arc.hexnet.SouthIn;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SLoopback extends Thread implements SouthIn {
    private Address address;

    private NorthIn south;

    private Queue<Packet> toReturn = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

    public SLoopback(Address address) {
        this.address = address;
    }

    @Override
    public void run() {
        while (run) {
            boolean sentS = sendSouth();
            if (!sentS) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    // noop
                }
            }
        }
    }

    protected boolean sendSouth() {
        Packet nextPacket = toReturn.poll();
        if (nextPacket == null) {
            return false;
        }
        nextPacket.markPath(getAddress());
        south.fromNorthThru(nextPacket);
        return true;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void fromSouthThru(Packet packet) {
        toReturn.add(packet);
    }

    @Override
    public void fromSouthTurn(Packet packet) {
        toReturn.add(packet);
    }

    @Override
    public void attachSouth(NorthIn south) {
        this.south = south;
    }

    @Override
    public boolean hasNorth() {
        return false;
    }

    @Override
    public boolean hasSouth() {
        return true;
    }

    @Override
    public boolean hasWest() {
        return false;
    }

    @Override
    public boolean hasEast() {
        return false;
    }

    @Override
    public String getLabel() {
        return "SL";
    }
}
