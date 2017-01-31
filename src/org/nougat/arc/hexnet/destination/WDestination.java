package org.nougat.arc.hexnet.destination;

import org.nougat.arc.hexnet.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A Destination which is connected to a junction to the west.
 */
public class WDestination extends Thread implements WestIn, Sender {
    private Address address;

    // Adjacent junctions; We flip directions (junction to the south needs a NorthIn)
    private EastIn west;

    private Queue<Packet> toWest = new ConcurrentLinkedDeque<>();
    private Queue<Packet> inbox = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

    public WDestination(Address address) {
        this.address = address;
    }

    @Override
    public void run() {
        while (run) {
            boolean read = readMail();
            boolean sentW = sendWest();
            if (!read && !sentW) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    // noop
                }
            }
        }
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
        return address;
    }

    @Override
    public void fromWestThru(Packet packet) {
        inbox.add(packet);
    }

    @Override
    public void fromWestTurn(Packet packet) {
        inbox.add(packet);
    }

    @Override
    public void attachWest(EastIn west) {
        this.west = west;
    }

    public void sendPacket(int payload, Address destination) {
        System.out.println(String.format("%s: send %d to %s", address.asString(), payload, destination.asString()));
        toWest.add(new Packet(payload, address, destination));
    }
}
