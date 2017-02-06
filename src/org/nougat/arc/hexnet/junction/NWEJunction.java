package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.*;

import java.util.concurrent.ExecutorService;

/**
 * This is a junction that has junctions to the north, west and east.
 * The junctions to the west and the east have destinations to their north.
 * The junction to the north has a destination to the east.
 */
public class NWEJunction extends Junction {
    public NWEJunction(Address address, ExecutorService executor) {
        super(address, executor);
    }

    @Override
    public void fromEastThru(Packet packet) {
        toWest.add(packet);
    }

    @Override
    public void fromEastTurn(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void fromNorthThru(Packet packet) {
        toWest.add(packet);
    }

    @Override
    public void fromNorthTurn(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public void fromWestTurn(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void fromWestThru(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public boolean hasNorth() {
        return true;
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
        return true;
    }

    @Override
    public String getLabel() {
        return "NWE";
    }

    @Override
    public void fromSouthThru(Packet packet) {
        
    }

    @Override
    public void fromSouthTurn(Packet packet) {

    }

    @Override
    protected void sendNorth(Packet packet) {
        packet.markPath(getAddress());
        if (packet.destination.equals(north.getAddress())) {
            north.fromSouthTurn(packet);
        }
        else {
            north.fromSouthThru(packet);
        }
        executor.submit(sendNorthTask);
    }

    @Override
    protected void sendSouth(Packet packet) {

    }

    @Override
    protected void sendWest(Packet packet) {
        packet.markPath(getAddress());
        if (packet.destination.equals(west.getAddress())) {
            west.fromEastTurn(packet);
        }
        else {
            west.fromEastThru(packet);
        }
        executor.submit(sendWestTask);
    }

    @Override
    protected void sendEast(Packet packet) {
        packet.markPath(getAddress());
        if (packet.destination.equals(east.getAddress())) {
            east.fromWestTurn(packet);
        }
        else {
            east.fromWestThru(packet);
        }
        executor.submit(sendEastTask);
    }
}
