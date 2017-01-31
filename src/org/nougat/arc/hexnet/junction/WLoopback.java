package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.EastIn;
import org.nougat.arc.hexnet.Packet;
import org.nougat.arc.hexnet.WestIn;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class WLoopback extends Thread implements WestIn {
    private Address address;

    private EastIn west;

    private Queue<Packet> toReturn = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

    public WLoopback(Address address) {
        this.address = address;
    }

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

    protected boolean sendWest() {
        Packet nextPacket = toReturn.poll();
        if (nextPacket == null) {
            return false;
        }
        nextPacket.markPath(getAddress());
        west.fromEastThru(nextPacket);
        return true;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void fromWestThru(Packet packet) {
        toReturn.add(packet);
    }

    @Override
    public void fromWestTurn(Packet packet) {
        toReturn.add(packet);
    }

    @Override
    public void attachWest(EastIn west) {
        this.west = west;
    }
}