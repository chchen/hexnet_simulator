package org.nougat.arc.hexnet.destination;

import org.nougat.arc.hexnet.*;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

/**
 * A Destination which is connected to a junction to the west.
 */
public class WDestination extends Junction implements Sender {
    private Queue<Packet> tracePackets;

    private Semaphore permit;
    
    public WDestination(Address address, Queue<Packet> tracePackets, Semaphore permit, ExecutorService executor) {
        super(address, executor);
        this.tracePackets = tracePackets;
        this.permit = permit;
    }

    @Override
    protected void sendEast(Packet packet) {
        System.out.println(String.format(
                "%s: recv %d from %s in %d ms via %s",
                address.asString(),
                packet.payload,
                packet.source.asString(),
                packet.latencyMs(),
                packet.getPathString()
        ));
        tracePackets.offer(packet);
        permit.release();
        executor.submit(sendEastTask);
    }

    @Override
    protected void sendWest(Packet packet) {
        if (packet.destination.yLessThan(west.getAddress())) {
            west.fromEastTurn(packet);
        }
        else {
            west.fromEastThru(packet);
        }
        executor.submit(sendWestTask);
    }

    @Override
    public void fromWestThru(Packet packet) {
        toEast.add(packet);
    }

    @Override
    public void fromWestTurn(Packet packet) {
        toEast.add(packet);
    }

    public void sendPacket(int payload, Address destination) {
        try {
            permit.acquire();
            System.out.println(String.format("%s: send %d to %s", address.asString(), payload, destination.asString()));
            toWest.add(new Packet(payload, address, destination));
        }
        catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
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
        return true;
    }

    @Override
    public String getLabel() {
        return "";
    }

    @Override
    public void fromEastThru(Packet packet) {

    }

    @Override
    public void fromNorthThru(Packet packet) {

    }

    @Override
    public void fromSouthThru(Packet packet) {

    }

    @Override
    public void fromEastTurn(Packet packet) {

    }

    @Override
    public void fromNorthTurn(Packet packet) {

    }

    @Override
    public void fromSouthTurn(Packet packet) {

    }

    @Override
    protected void sendNorth(Packet packet) {

    }

    @Override
    protected void sendSouth(Packet packet) {

    }
}
