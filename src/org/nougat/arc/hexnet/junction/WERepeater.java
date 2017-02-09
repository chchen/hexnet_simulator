package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Junction;
import org.nougat.arc.hexnet.Packet;

import java.util.concurrent.ExecutorService;

public class WERepeater extends Junction {
    public WERepeater(Address address, ExecutorService executor) {
        super(address, executor);
    }

    @Override
    public void fromEastThru(Packet packet) {
        toWest.add(packet);
    }

    @Override
    public void fromWestThru(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public boolean hasNorth() {
        return false;
    }

    @Override
    public void fromNorthThru(Packet packet) {

    }

    @Override
    public void fromSouthThru(Packet packet) {

    }

    @Override
    public boolean hasSouth() {
        return false;
    }

    @Override
    public void fromEastTurn(Packet packet) {
        throw new RuntimeException("Illegal turn from East at " + address.asString());

    }

    @Override
    public void fromWestTurn(Packet packet) {
        throw new RuntimeException("Illegal turn from West at " + address.asString());
    }

    @Override
    public void fromNorthTurn(Packet packet) {

    }

    @Override
    public void fromSouthTurn(Packet packet) {

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
        return "WE";
    }

    @Override
    protected void sendNorth(Packet packet) {

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
