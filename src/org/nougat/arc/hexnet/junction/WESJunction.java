package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.*;

import java.util.concurrent.*;

/**
 *  This is a junction that has junctions to the west, east, and south.
 *  To the south is a junction that has a destination to the east.
 *  The the west and east are junctions that have a destination to the north.
 */
public class WESJunction extends Junction {
    public WESJunction(Address address, ExecutorService executor) {
        super(address, executor);
    }

    @Override
    public void fromWestThru(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public void fromWestTurn(Packet packet) {
        toSouth.add(packet);
    }

    @Override
    public void fromEastThru(Packet packet) {
        toWest.add(packet);
    }

    @Override
    public void fromEastTurn(Packet packet) {
        toSouth.add(packet);
    }

    @Override
    public void fromSouthThru(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public void fromSouthTurn(Packet packet) {
        toWest.add(packet);
    }

    @Override
    public boolean hasNorth() {
        return false;
    }

    @Override
    public boolean hasSouth() {
        return true;
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
        return "WES";
    }

    @Override
    public void fromNorthThru(Packet packet) {}

    @Override
    public void fromNorthTurn(Packet packet) {}
}
