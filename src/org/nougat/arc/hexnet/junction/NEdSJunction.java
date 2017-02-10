package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.*;

import java.util.concurrent.ExecutorService;

// This is a junction that has junctions to the north and south, and a destination to the east.
public class NEdSJunction extends Junction {
    public NEdSJunction(Address address, ExecutorService executor) {
        super(address, executor);
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
    public boolean destinationIsEast() {
        return true;
    }
}
