package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Packet;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A Destination which is connected to a junction to the south.
 */
public class SDestination extends Thread implements SouthIn {
    private Address address;

    // Adjacent junctions; We flip directions (junction to the south needs a NorthIn)
    private NorthIn south;

    private Queue<Packet> toSouth = new ConcurrentLinkedDeque<>();
    private Queue<Packet> inbox = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

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

    private boolean sendSouth() {
        Packet nextPacket = toSouth.poll();
        if (nextPacket == null) {
            return false;
        }
        if (nextPacket.destination.xGreaterThan(south.getAddress())) {
            south.fromNorthTurn(nextPacket);
        }
        else {
            south.fromNorthThru(nextPacket);
        }
        return true;
    }

    @Override
    public Address getAddress() {
        return null;
    }

    @Override
    public void fromSouthThru(Packet packet) {
        inbox.add(packet);
    }

    @Override
    public void fromSouthTurn(Packet packet) {
        inbox.add(packet);
    }
}