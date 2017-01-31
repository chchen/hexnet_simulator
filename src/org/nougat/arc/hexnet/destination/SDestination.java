package org.nougat.arc.hexnet.destination;

import org.nougat.arc.hexnet.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A Destination which is connected to a junction to the south.
 */
public class SDestination extends Thread implements SouthIn, Sender {
    private Address address;

    // Adjacent junctions; We flip directions (junction to the south needs a NorthIn)
    private NorthIn south;

    private Queue<Packet> toSouth = new ConcurrentLinkedDeque<>();
    private Queue<Packet> inbox = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

    public SDestination(Address address) {
        this.address = address;
    }

    @Override
    public void attachSouth(NorthIn south) {
        this.south = south;
    }

    @Override
    public void run() {
        while (run) {
            boolean read = readMail();
            boolean sentS = sendSouth();
            if (!read && !sentS) {
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

    private boolean readMail() {
        Packet nextPacket = inbox.poll();
        if (nextPacket == null) {
            return false;
        }
        System.out.println(String.format(
                "%s: recv %d from %s in %d ms via %s",
                address.asString(),
                nextPacket.payload,
                nextPacket.source.asString(),
                nextPacket.latencyMs(),
                nextPacket.getPathString()
        ));
        return true;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void fromSouthThru(Packet packet) {
        inbox.add(packet);
    }

    @Override
    public void fromSouthTurn(Packet packet) {
        inbox.add(packet);
    }

    @Override
    public void sendPacket(int payload, Address destination) {
        System.out.println(String.format("%s: send %d to %s", address.asString(), payload, destination.asString()));
        toSouth.add(new Packet(payload, address, destination));
    }
}