package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A Destination which is connected to a junction to the west.
 */
public class WDestination extends Thread implements WestIn {
    private Address address;

    // Adjacent junctions; We flip directions (junction to the south needs a NorthIn)
    private EastIn west;

    private Queue<Packet> toWest = new ConcurrentLinkedDeque<>();
    private Queue<Packet> inbox = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

    @Override
    public void run() {
        while (run) {
            boolean sentW = sendWest();
            if (!sentW) {
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
        if (nextPacket.destination.yLessThan(west.getAddress())) {
            west.fromEastTurn(nextPacket);
        }
        else {
            west.fromEastThru(nextPacket);
        }
        return true;
    }

    @Override
    public Address getAddress() {
        return null;
    }

    @Override
    public void fromWestThru(Packet packet) {
        inbox.add(packet);
    }

    @Override
    public void fromWestTurn(Packet packet) {
        inbox.add(packet);
    }
}
