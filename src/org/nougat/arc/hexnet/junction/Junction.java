package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class Junction extends Thread implements NorthIn, SouthIn, WestIn, EastIn {
    protected ExecutorService executor;
    protected Address address;

    protected NorthIn south;
    protected SouthIn north;
    protected EastIn west;
    protected WestIn east;

    protected BlockingQueue<Packet> toNorth = new LinkedBlockingQueue<>();
    protected BlockingQueue<Packet> toSouth = new LinkedBlockingQueue<>();
    protected BlockingQueue<Packet> toWest = new LinkedBlockingQueue<>();
    protected BlockingQueue<Packet> toEast = new LinkedBlockingQueue<>();

    public Junction(Address address, ExecutorService executor) {
        this.address = address;
        this.executor = executor;
    }

    @Override
    public void run() {
        if (hasNorth()) {
            executor.submit(sendNorthTask);
        }
        if (hasSouth()) {
            executor.submit(sendSouthTask);
        }
        if (hasWest()) {
            executor.submit(sendWestTask);
        }
        if (hasEast()) {
            executor.submit(sendEastTask);
        }
    }

    @Override
    public final Address getAddress() {
        return address;
    }

    @Override
    public final void attachNorth(SouthIn north) {
        this.north = north;
    }

    @Override
    public final void attachSouth(NorthIn south) {
        this.south = south;
    }

    @Override
    public final void attachWest(EastIn west) {
        this.west = west;
    }

    @Override
    public final void attachEast(WestIn east) {
        this.east = east;
    }

    protected final Runnable sendNorthTask = () -> {
        try {
            Packet packet = toNorth.poll(100, TimeUnit.MILLISECONDS);
            if (packet != null) {
                sendNorth(packet);
            }
            else {
                reSendNorthTask();
            }
        }
        catch (InterruptedException e) {
            //
        }
    };

    private void reSendNorthTask() {
        executor.submit(sendNorthTask);
    }

    protected final Runnable sendSouthTask = () -> {
        try {
            Packet packet = toSouth.poll(100, TimeUnit.MILLISECONDS);
            if (packet != null) {
                sendSouth(packet);
            }
            else {
                reSendSouthTask();
            }
        }
        catch (InterruptedException e) {
            //
        }
    };

    private void reSendSouthTask() {
        executor.submit(sendSouthTask);
    }

    protected final Runnable sendWestTask = () -> {
        try {
            Packet packet = toWest.poll(100, TimeUnit.MILLISECONDS);
            if (packet != null) {
                sendWest(packet);
            }
            else {
                reSendWestTask();
            }
        }
        catch (InterruptedException e) {
            //
        }
    };

    private void reSendWestTask() {
        executor.submit(sendWestTask);
    }

    protected final Runnable sendEastTask = () -> {
        try {
            Packet packet = toEast.poll(100, TimeUnit.MILLISECONDS);
            if (packet != null) {
                sendEast(packet);
            }
            else {
                reSendEastTask();
            }
        }
        catch (InterruptedException e) {
            //
        }
    };

    private void reSendEastTask() {
        executor.submit(sendEastTask);
    }

    protected abstract void sendNorth(Packet packet);

    protected abstract void sendSouth(Packet packet);

    protected abstract void sendWest(Packet packet);

    protected abstract void sendEast(Packet packet);

}
