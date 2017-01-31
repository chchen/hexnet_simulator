package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

// This is a junction that has junctions to the north and south, and a destination to the east.
public class NEdSJunction extends Thread implements NorthIn, EastIn, SouthIn {
    private Address address;

    // Adjacent junctions; We flip directions (junction to the south needs a NorthIn)
    private SouthIn north;
    private WestIn east;
    private NorthIn south;

    private Queue<Packet> toNorth = new ConcurrentLinkedDeque<>();
    private Queue<Packet> toEast = new ConcurrentLinkedDeque<>();
    private Queue<Packet> toSouth = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

    public NEdSJunction(Address address) {
        this.address = address;
    }

    @Override
    public void attachEast(WestIn east) {
        this.east = east;
    }

    @Override
    public void attachNorth(SouthIn north) {
        this.north = north;
    }

    @Override
    public void attachSouth(NorthIn south) {
        this.south = south;
    }

    @Override
    public void run() {
        while (run) {
            boolean sentN = sendNorth();
            boolean sentE = sendEast();
            boolean sentS = sendSouth();
            if (!sentN && !sentE && !sentS) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    // noop
                }
            }
        }
    }

    private boolean sendNorth() {
        Packet nextPacket = toNorth.poll();
        if (nextPacket == null) {
            return false;
        }
        nextPacket.markPath(getAddress());
        if (nextPacket.destination.xLessThan(north.getAddress())) {
            north.fromSouthTurn(nextPacket);
        }
        else {
            north.fromSouthThru(nextPacket);
        }
        return true;
    }

    private boolean sendSouth() {
        Packet nextPacket = toSouth.poll();
        if (nextPacket == null) {
            return false;
        }
        nextPacket.markPath(getAddress());
        if (nextPacket.destination.xLessThan(south.getAddress())) {
            south.fromNorthThru(nextPacket);
        }
        else {
            south.fromNorthTurn(nextPacket);
        }
        return true;
    }

    private boolean sendEast() {
        Packet nextPacket = toEast.poll();
        if (nextPacket == null) {
            return false;
        }
        nextPacket.markPath(getAddress());
        east.fromWestThru(nextPacket);
        return true;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void fromEastThru(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void fromEastTurn(Packet packet) {
        toSouth.add(packet);
    }

    @Override
    public void fromNorthThru(Packet packet) {
        toSouth.add(packet);
    }

    @Override
    public void fromNorthTurn(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public void fromSouthThru(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void fromSouthTurn(Packet packet) {
        toEast.add(packet);
    }
}
