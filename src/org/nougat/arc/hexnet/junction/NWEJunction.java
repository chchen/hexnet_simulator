package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This is a junction that has junctions to the north, west and east.
 * The junctions to the west and the east have destinations to their north.
 * The junction to the north has a destination to the east.
 */
public class NWEJunction extends Thread implements NorthIn, WestIn, EastIn {
    private Address address;

    // Adjacent junctions; We flip directions (junction to the south needs a NorthIn)
    protected EastIn west;
    protected WestIn east;
    protected SouthIn north;

    protected Queue<Packet> toNorth = new ConcurrentLinkedDeque<>();
    protected Queue<Packet> toWest = new ConcurrentLinkedDeque<>();
    protected Queue<Packet> toEast = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

    @Override
    public void run() {
        while (run) {
            boolean sentN = sendNorth();
            boolean sentW = sendWest();
            boolean sentE = sendEast();
            if (!sentN && !sentW && !sentE) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    // noop
                }
            }
        }
    }

    protected boolean sendNorth() {
        Packet nextPacket = toNorth.poll();
        if (nextPacket == null) {
            return false;
        }
        if (nextPacket.destination.equals(north.getAddress())) {
            north.fromSouthTurn(nextPacket);
        }
        else {
            north.fromSouthThru(nextPacket);
        }
        return true;
    }

    protected boolean sendWest() {
        Packet nextPacket = toWest.poll();
        if (nextPacket == null) {
            return false;
        }
        if (nextPacket.destination.equals(west.getAddress())) {
            west.fromEastTurn(nextPacket);
        }
        else {
            west.fromEastThru(nextPacket);
        }
        return true;
    }

    protected boolean sendEast() {
        Packet nextPacket = toEast.poll();
        if (nextPacket == null) {
            return false;
        }
        if (nextPacket.destination.equals(east.getAddress())) {
            east.fromWestTurn(nextPacket);
        }
        else {
            east.fromWestThru(nextPacket);
        }
        return true;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void fromEastThru(Packet packet) {
        toWest.add(packet);
    }

    @Override
    public void fromEastTurn(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void fromNorthThru(Packet packet) {
        toWest.add(packet);
    }

    @Override
    public void fromNorthTurn(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public void fromWestTurn(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void fromWestThru(Packet packet) {
        toEast.add(packet);
    }
}
