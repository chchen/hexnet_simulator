package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.EastIn;
import org.nougat.arc.hexnet.Packet;
import org.nougat.arc.hexnet.WestIn;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ELoopback extends Thread implements EastIn {
    private Address address;

    private WestIn east;

    private Queue<Packet> toReturn = new ConcurrentLinkedDeque<>();

    private volatile boolean run = true;

    public ELoopback(Address address) {
        this.address = address;
    }

    @Override
    public void run() {
        while (run) {
            boolean sentE = sendEast();
            if (!sentE) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {
                    // noop
                }
            }
        }
    }

    protected boolean sendEast() {
        Packet nextPacket = toReturn.poll();
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
        toReturn.add(packet);
    }

    @Override
    public void fromEastTurn(Packet packet) {
        toReturn.add(packet);
    }

    @Override
    public void attachEast(WestIn east) {
        this.east = east;
    }
}