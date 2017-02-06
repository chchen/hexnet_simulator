package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.*;

import java.util.concurrent.ExecutorService;

// This is a junction that has junctions to the north and south, and a destination to the east.
public class NEdSJunction extends Junction {
    public NEdSJunction(Address address, ExecutorService executor) {
        super(address, executor);
    }

    @Override
    protected void sendNorth(Packet packet) {
        packet.markPath(getAddress());
        if (packet.destination.xLessThan(north.getAddress())) {
            north.fromSouthTurn(packet);
        }
        else {
            north.fromSouthThru(packet);
        }
        executor.submit(sendNorthTask);
    }

    @Override
    protected void sendSouth(Packet packet) {
        packet.markPath(getAddress());
        if (packet.destination.xLessThan(south.getAddress())) {
            south.fromNorthThru(packet);
        }
        else {
            south.fromNorthTurn(packet);
        }
        executor.submit(sendSouthTask);
    }

    @Override
    protected void sendEast(Packet packet) {
        packet.markPath(getAddress());
        east.fromWestThru(packet);
        executor.submit(sendEastTask);
    }

    @Override
    public void fromEastThru(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void fromEastTurn(Packet packet) {
        toSouth.add(packet);
    }

    @Override
    public void fromNorthThru(Packet packet) {
        toSouth.add(packet);
    }

    @Override
    public void fromNorthTurn(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public void fromSouthThru(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void fromSouthTurn(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public boolean hasNorth() {
        return true;
    }

    @Override
    public boolean hasSouth() {
        return true;
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
        return "NS dest E";
    }

    @Override
    public void fromWestThru(Packet packet) {

    }

    @Override
    public void fromWestTurn(Packet packet) {

    }

    @Override
    protected void sendWest(Packet packet) {

    }
}
