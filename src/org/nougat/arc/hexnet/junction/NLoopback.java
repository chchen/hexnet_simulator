package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Packet;

import java.util.concurrent.ExecutorService;

public class NLoopback extends Junction {
    public NLoopback(Address address, ExecutorService executor) {
        super(address, executor);
    }

    @Override
    public void fromNorthThru(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void fromNorthTurn(Packet packet) {
        toNorth.add(packet);
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
        return false;
    }

    @Override
    public boolean hasEast() {
        return false;
    }

    @Override
    public String getLabel() {
        return "NL";
    }

    @Override
    public void fromEastThru(Packet packet) {

    }

    @Override
    public void fromWestThru(Packet packet) {

    }

    @Override
    public void fromSouthThru(Packet packet) {

    }

    @Override
    public void fromEastTurn(Packet packet) {

    }

    @Override
    public void fromWestTurn(Packet packet) {

    }

    @Override
    public void fromSouthTurn(Packet packet) {

    }

    @Override
    protected void sendNorth(Packet packet) {
        packet.markPath(getAddress());
        north.fromSouthThru(packet);
        executor.submit(sendNorthTask);
    }

    @Override
    protected void sendSouth(Packet packet) {

    }

    @Override
    protected void sendWest(Packet packet) {

    }

    @Override
    protected void sendEast(Packet packet) {

    }
}