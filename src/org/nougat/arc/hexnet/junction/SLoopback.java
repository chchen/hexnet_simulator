package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Junction;
import org.nougat.arc.hexnet.Packet;

import java.util.concurrent.ExecutorService;

public class SLoopback extends Junction {
    public SLoopback(Address address, ExecutorService executor) {
        super(address, executor);
    }

    @Override
    protected void sendSouth(Packet packet) {
        packet.markPath(getAddress());
        south.fromNorthThru(packet);
        executor.submit(sendSouthTask);
    }

    @Override
    public void fromSouthThru(Packet packet) {
        toSouth.add(packet);
    }

    @Override
    public void fromSouthTurn(Packet packet) {
        toSouth.add(packet);
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
        return false;
    }

    @Override
    public boolean hasEast() {
        return false;
    }

    @Override
    public String getLabel() {
        return "SL";
    }

    @Override
    public void fromEastThru(Packet packet) {

    }

    @Override
    public void fromWestThru(Packet packet) {

    }

    @Override
    public void fromNorthThru(Packet packet) {

    }

    @Override
    public void fromEastTurn(Packet packet) {

    }

    @Override
    public void fromWestTurn(Packet packet) {

    }

    @Override
    public void fromNorthTurn(Packet packet) {

    }

    @Override
    protected void sendNorth(Packet packet) {

    }

    @Override
    protected void sendWest(Packet packet) {

    }

    @Override
    protected void sendEast(Packet packet) {

    }
}
