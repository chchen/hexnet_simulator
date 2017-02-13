package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Junction;
import org.nougat.arc.hexnet.Packet;

import java.util.concurrent.*;

public class WLoopback extends Junction {
    public WLoopback(Address address, ExecutorService executor) {
        super(address, executor);
    }

    @Override
    protected void sendWest(Packet packet) {
        traversals.incrementAndGet();
        packet.markPath(getAddress());
        west.fromEastThru(packet);
        executor.submit(sendWestTask);
    }

    @Override
    public void fromWestThru(Packet packet) {
        toWest.add(packet);
    }

    @Override
    public void fromWestTurn(Packet packet) {
        toWest.add(packet);
    }

    @Override
    public boolean hasNorth() {
        return false;
    }

    @Override
    public boolean hasSouth() {
        return false;
    }

    @Override
    public boolean hasWest() {
        return true;
    }

    @Override
    public boolean hasEast() {
        return false;
    }

    @Override
    public String getLabel() {
        return "WL";
    }

    @Override
    public void fromEastThru(Packet packet) {}

    @Override
    public void fromNorthThru(Packet packet) {}

    @Override
    public void fromSouthThru(Packet packet) {}

    @Override
    public void fromEastTurn(Packet packet) {}

    @Override
    public void fromNorthTurn(Packet packet) {}

    @Override
    public void fromSouthTurn(Packet packet) {}
}