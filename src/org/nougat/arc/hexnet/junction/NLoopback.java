package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.NorthIn;
import org.nougat.arc.hexnet.Packet;
import org.nougat.arc.hexnet.SouthIn;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NLoopback extends Thread implements NorthIn {
    private Address address;

    private SouthIn north;

    private Queue<Packet> toReturn = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

    public NLoopback(Address address) {
        this.address = address;
    }

    @Override
    public void run() {
        while (run) {
            boolean sentN = sendNouth();
            if (!sentN) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    // noop
                }
            }
        }
    }

    protected boolean sendNouth() {
        Packet nextPacket = toReturn.poll();
        if (nextPacket == null) {
            return false;
        }
        nextPacket.markPath(getAddress());
        north.fromSouthThru(nextPacket);
        return true;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void fromNorthThru(Packet packet) {
        toReturn.add(packet);
    }

    @Override
    public void fromNorthTurn(Packet packet) {
        toReturn.add(packet);
    }

    @Override
    public void attachNorth(SouthIn north) {
        this.north = north;
    }

    @Override
    public boolean hasNorth() {
        return true;
    }

    @Override
    public boolean hasSouth() {
        return false;
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
        return "NL";
    }
}