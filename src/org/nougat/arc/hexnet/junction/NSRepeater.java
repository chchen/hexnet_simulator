package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Junction;
import org.nougat.arc.hexnet.Packet;

import java.util.concurrent.ExecutorService;

public class NSRepeater extends Junction {
    public NSRepeater(Address address, ExecutorService executor) {
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
    public void fromEastThru(Packet packet) {

    }

    @Override
    public void fromWestThru(Packet packet) {

    }

    @Override
    public boolean hasNorth() {
        return true;
    }

    @Override
    public void fromNorthThru(Packet packet) {
        toSouth.add(packet);
    }

    @Override
    public void fromSouthThru(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public boolean hasSouth() {
        return true;
    }

    @Override
    public void fromEastTurn(Packet packet) {

    }

    @Override
    public void fromWestTurn(Packet packet) {

    }

    @Override
    public void fromNorthTurn(Packet packet) {
        throw new RuntimeException("Illegal turn from North at " + address.asString());
    }

    @Override
    public void fromSouthTurn(Packet packet) {
        throw new RuntimeException("Illegal turn from South at " + address.asString());
    }

    @Override
    public boolean hasWest() {
        return false;
    }

    @Override
    public boolean hasEast() {
        return false;
    }

    @Override
    public String getLabel() {
        return "NS";
    }

    @Override
    protected void sendWest(Packet packet) {

    }

    @Override
    protected void sendEast(Packet packet) {

    }
}
