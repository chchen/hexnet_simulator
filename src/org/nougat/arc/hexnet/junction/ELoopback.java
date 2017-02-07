package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Junction;
import org.nougat.arc.hexnet.Packet;

import java.util.concurrent.ExecutorService;

public class ELoopback extends Junction {
    public ELoopback(Address address, ExecutorService executor) {
        super(address, executor);
    }

    @Override
    protected void sendEast(Packet packet) {
        packet.markPath(getAddress());
        east.fromWestThru(packet);
        executor.submit(sendEastTask);
    }

    @Override
    public void fromEastThru(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public void fromEastTurn(Packet packet) {
        toEast.add(packet);
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
        return false;
    }

    @Override
    public boolean hasEast() {
        return true;
    }

    @Override
    public String getLabel() {
        return "EL";
    }

    @Override
    public void fromWestThru(Packet packet) {}

    @Override
    public void fromNorthThru(Packet packet) {}

    @Override
    public void fromSouthThru(Packet packet) {}

    @Override
    public void fromWestTurn(Packet packet) {}

    @Override
    public void fromNorthTurn(Packet packet) {}

    @Override
    public void fromSouthTurn(Packet packet) {}

    @Override
    protected void sendNorth(Packet packet) {}

    @Override
    protected void sendSouth(Packet packet) {}

    @Override
    protected void sendWest(Packet packet) {}
}