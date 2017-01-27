package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 *  This is a junction that has junctions to the west, east, and south.
 *  To the south is a junction that has a destination to the east.
 *  The the west and east are junctions that have a destination to the north.
 */
public class WESJunction extends Thread implements WestIn, EastIn, SouthIn {
    private Address address;

    // Adjacent junctions; We flip directions (junction to the south needs a NorthIn)
    private EastIn west;
    private WestIn east;
    private NorthIn south;

    private Queue<Packet> toWest = new ConcurrentLinkedDeque<>();
    private Queue<Packet> toEast = new ConcurrentLinkedDeque<>();
    private Queue<Packet> toSouth = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

    @Override
    public void run() {
        while (run) {
            boolean sentW = sendWest();
            boolean sentE = sendEast();
            boolean sentS = sendSouth();
            if (!sentW && !sentE && !sentS) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    // noop
                }
            }
        }
    }

    private boolean sendWest() {
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

    private boolean sendEast() {
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

    private boolean sendSouth() {
        Packet nextPacket = toSouth.poll();
        if (nextPacket == null) {
            return false;
        }
        if (nextPacket.destination.equals(south.getAddress())) {
            south.fromNorthTurn(nextPacket);
        }
        else {
            south.fromNorthThru(nextPacket);
        }
        return true;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void fromWestThru(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public void fromWestTurn(Packet packet) {
        toSouth.add(packet);
    }

    @Override
    public void fromEastThru(Packet packet) {
        toWest.add(packet);
    }

    @Override
    public void fromEastTurn(Packet packet) {
        toSouth.add(packet);
    }

    @Override
    public void fromSouthThru(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public void fromSouthTurn(Packet packet) {
        toWest.add(packet);
    }
}
