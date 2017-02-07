package org.nougat.arc.hexnet.destination;

import org.nougat.arc.hexnet.*;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

/**
 * A Destination which is connected to a junction to the south.
 */
public class SDestination extends Junction implements Sender {
    private Queue<Packet> tracePackets;

    private Semaphore permit;
    
    public SDestination(Address address, Queue<Packet> tracePackets, Semaphore permit, ExecutorService executor) {
        super(address, executor);
        this.tracePackets = tracePackets;
        this.permit = permit;
    }

    @Override
    protected void sendSouth(Packet packet) {
        if (packet.destination.xGreaterThan(south.getAddress())) {
            south.fromNorthTurn(packet);
        }
        else {
            south.fromNorthThru(packet);
        }
        executor.submit(sendSouthTask);
    }

    @Override
    protected void sendNorth(Packet packet) {
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
        executor.submit(sendNorthTask);
    }

    @Override
    public void fromSouthThru(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void fromSouthTurn(Packet packet) {
        toNorth.add(packet);
    }

    @Override
    public void sendPacket(int payload, Address destination) {
        try {
            permit.acquire();
            System.out.println(String.format("%s: send %d to %s", address.asString(), payload, destination.asString()));
            toSouth.add(new Packet(payload, address, destination));
        }
        catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
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
        return false;
    }

    @Override
    public String getLabel() {
        return "";
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
    protected void sendWest(Packet packet) {

    }

    @Override
    protected void sendEast(Packet packet) {

    }
}