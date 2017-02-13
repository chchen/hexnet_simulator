package org.nougat.arc.hexnet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Junction extends Thread implements NorthIn, SouthIn, WestIn, EastIn {
    protected ExecutorService executor;
    protected Address address;

    protected NorthIn south;
    protected SouthIn north;
    protected EastIn west;
    protected WestIn east;

    protected Router northRouter;
    protected Router southRouter;
    protected Router westRouter;
    protected Router eastRouter;

    protected AtomicInteger traversals = new AtomicInteger();

    protected BlockingQueue<Packet> toNorth = new LinkedBlockingQueue<>();
    protected BlockingQueue<Packet> toSouth = new LinkedBlockingQueue<>();
    protected BlockingQueue<Packet> toWest = new LinkedBlockingQueue<>();
    protected BlockingQueue<Packet> toEast = new LinkedBlockingQueue<>();
    protected BlockingQueue<Packet> mailbox = new LinkedBlockingQueue<>();

    public Junction(Address address, ExecutorService executor) {
        this.address = address;
        this.executor = executor;
    }

    @Override
    public void run() {
        if (hasNorth()) {
            northRouter = RouterFactory.northRouter(north);
            executor.submit(sendNorthTask);
        }
        if (hasSouth()) {
            southRouter = RouterFactory.southRouter(south);
            executor.submit(sendSouthTask);
        }
        if (hasWest()) {
            westRouter = RouterFactory.westRouter(west);
            executor.submit(sendWestTask);
        }
        if (hasEast()) {
            eastRouter = RouterFactory.eastRouter(east);
            executor.submit(sendEastTask);
        }
        if (isDestination()) {
            executor.submit(readMailboxTask);
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

    protected void reSendNorthTask() {
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

    protected void reSendSouthTask() {
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

    protected void reSendWestTask() {
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

    protected void reSendEastTask() {
        executor.submit(sendEastTask);
    }

    protected final Runnable readMailboxTask = () -> {
        try {
            Packet packet = mailbox.poll(100, TimeUnit.MILLISECONDS);
            if (packet != null) {
                readMailbox(packet);
            }
            else {
                reReadMailboxTask();
            }
        }
        catch (InterruptedException e) {
            //
        }
    };

    protected void reReadMailboxTask() {
        executor.submit(readMailboxTask);
    }

    protected void readMailbox(Packet packet) {};

    @Override
    public boolean destinationIsWest() {
        return false;
    }

    @Override
    public boolean destinationIsEast() {
        return false;
    }

    @Override
    public boolean destinationIsNorth() {
        return false;
    }

    @Override
    public boolean destinationIsSouth() {
        return false;
    }

    @Override
    public boolean isDestination() {
        return false;
    }

    protected void sendNorth(Packet packet) {
        System.out.println(String.format("%s: routing %s -> %s north", getAddress().asString(), packet.source.asString(), packet.destination.asString()));
        traversals.incrementAndGet();
        packet.markPath(getAddress());
        northRouter.route(packet);
        reSendNorthTask();
    }

    protected void sendSouth(Packet packet) {
        System.out.println(String.format("%s: routing %s -> %s south", getAddress().asString(), packet.source.asString(), packet.destination.asString()));
        traversals.incrementAndGet();
        packet.markPath(getAddress());
        southRouter.route(packet);
        reSendSouthTask();
    }

    protected void sendWest(Packet packet) {
        System.out.println(String.format("%s: routing %s -> %s west", getAddress().asString(), packet.source.asString(), packet.destination.asString()));
        traversals.incrementAndGet();
        packet.markPath(getAddress());
        westRouter.route(packet);
        reSendWestTask();;
    }

    protected void sendEast(Packet packet) {
        System.out.println(String.format("%s: routing %s -> %s east", getAddress().asString(), packet.source.asString(), packet.destination.asString()));
        traversals.incrementAndGet();
        packet.markPath(getAddress());
        eastRouter.route(packet);
        reSendEastTask();
    }

    @Override
    public int getTraversals() {
        return traversals.get();
    }
}
