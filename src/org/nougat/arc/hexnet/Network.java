package org.nougat.arc.hexnet;

import org.nougat.arc.hexnet.destination.SDestination;
import org.nougat.arc.hexnet.destination.WDestination;
import org.nougat.arc.hexnet.junction.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * A packet-switched network
 */
public class Network {

    private Locatable[][] nodes;

    private final int rows;
    private final int columns;

    private final int xAddresses;
    private final int yAddresses;

    public List<Locatable> destinations;
    public List<Locatable> junctions;
    public List<Address> repeaterAddresses;

    private Queue<Packet> tracePackets;
    private Semaphore simulationRunning = new Semaphore(1);
    private ExecutorService executor;

    /**
     * Currently builds a 1+2n row by m column network.
     *
     * Lays out rows (two rows per length of bricks), one where the corners face up, and one where the corners face down
     * Between the rows where the corners are facing away, insert NEdSJunctions *between* the brick corners.
     * Between the rows where the corners are facing each other, insert NEdSJunctions at the corners
     * Each node has their address inserted into an array or hash table.
     * When wiring up the network, if the anticipated node is missing we add a loopback.
     * Since we add W and E loopbacks when we lay out the rows, the loopbacks added will be at the N and S ends
     *
     */
    public Network(Queue<Packet> tracePackets, int rows, int columns, List<Address> repeaterAddresses) {
        assert rows > 0;
        assert (rows - 1) % 2 == 0;
        assert columns > 0;
        this.rows = rows;
        this.columns = columns;
        this.repeaterAddresses = repeaterAddresses;

        xAddresses = 1 + (4 * columns) + 2;
        yAddresses = 1 + (2 * rows) + 2;
        System.out.println(String.format("Making network of %d rows %d columns", yAddresses, xAddresses));

        nodes = new Locatable[yAddresses][xAddresses];

        destinations = new ArrayList<>();
        junctions = new ArrayList<>();

        this.tracePackets = tracePackets;

        executor = Executors.newCachedThreadPool();

        boolean up = true;
        for (int yCoord = 1; yCoord < yAddresses; yCoord = yCoord + 2) {
            System.out.println("Generating for row " + yCoord);
            if (up) {
                rowUp(yCoord);
            }
            else {
                rowDown(yCoord);
            }
            up = !up;
        }

        for (int yCoord = 2; yCoord < yAddresses - 1; yCoord = yCoord + 2) {
            stitchRows(yCoord);
        }

        closeEndRow(1);
        closeEndRow(yAddresses - 2);
    }

    public void start() throws InterruptedException {
        for (Locatable d : destinations) {
            Thread t = (Thread) d;
            t.start();
        }
        for (Locatable d : junctions) {
            Thread t = (Thread) d;
            t.start();
        }
    }

    private void addJunction(Locatable newJunction) {
        System.out.println("Adding junction " + newJunction + " at " + newJunction.getAddress().asString());
        junctions.add(newJunction);
        nodes[newJunction.getAddress().getYCoord()][newJunction.getAddress().getXCoord()] = newJunction;
    }

    /**
     * Lay a row of junctions (and associated destinations) where the brick corners face north
     */
    private void rowUp(int yCoord) {
        EastIn last;
        ELoopback node0 = new ELoopback(new Address(0, yCoord), executor);
        addJunction(node0);
        NWEJunction node1 = new NWEJunction(new Address(1, yCoord), executor);
        addJunction(node1);
        stitchWE(node0, node1);
        last = node1;
        for (int xOffset = 2; xOffset < xAddresses - 1; xOffset = xOffset + 4) {
            EastIn node2 = placeNdWEJunction(last, xOffset, yCoord);

            WESJunction node3 = new WESJunction(new Address(xOffset + 1, yCoord), executor);
            addJunction(node3);
            stitchWE(node2, node3);

            EastIn node4 = placeNdWEJunction(node3, xOffset + 2, yCoord);

            NWEJunction node5 = new NWEJunction(new Address(xOffset + 3, yCoord), executor);
            addJunction(node5);
            stitchWE(node4, node5);

            last = node5;
        }
        WLoopback node6 = new WLoopback(new Address(last.getAddress().getXCoord() + 1, yCoord), executor);
        addJunction(node6);
        stitchWE(last, node6);
    }

    /**
     * Lay a row of junctions (and associated destinations) where the brick corners face south
     */
    private void rowDown(int yCoord) {
        EastIn last;
        ELoopback node0 = new ELoopback(new Address(0, yCoord), executor);
        addJunction(node0);
        WESJunction node1 = new WESJunction(new Address(1, yCoord), executor);
        addJunction(node1);
        stitchWE(node0, node1);
        last = node1;
        for (int xOffset = 2; xOffset < xAddresses - 1; xOffset = xOffset + 4) {
            EastIn node2 = placeNdWEJunction(last, xOffset, yCoord);

            NWEJunction node3 = new NWEJunction(new Address(xOffset + 1, yCoord), executor);
            addJunction(node3);
            stitchWE(node2, node3);

            EastIn node4 = placeNdWEJunction(node3, xOffset + 2, yCoord);

            WESJunction node5 = new WESJunction(new Address(xOffset + 3, yCoord), executor);
            addJunction(node5);
            stitchWE(node4, node5);

            last = node5;
        }
        WestIn node6 = new WLoopback(new Address(last.getAddress().getXCoord() + 1, yCoord), executor);
        addJunction(node6);
        stitchWE(last, node6);
    }

    private EastIn placeNdWEJunction(EastIn last, int xCoord, int yCoord) {
        boolean isRepeater = false;
        for (Address a : repeaterAddresses) {
            if (a.equals(xCoord, yCoord)) {
                isRepeater = true;
            }
        }

        if (isRepeater) {
            System.out.println(String.format("Repeater at (%d,%d)", xCoord, yCoord));
            WERepeater node = new WERepeater(new Address(xCoord, yCoord), executor);
            addJunction(node);
            stitchWE(last, node);
            return node;
        }
        else {
            NdWEJunction node = new NdWEJunction(new Address(xCoord, yCoord), executor);
            addJunction(node);
            stitchWE(last, node);

            SDestination dest = new SDestination(new Address(xCoord, yCoord), tracePackets, simulationRunning, executor);
            destinations.add(dest);
            stitchNS(dest, node);
            return node;
        }
    }

    /**
     * Stitches two rows together.
     *
     * Rule: if the yCoord is divisible by 4, then it's a "narrow" stitch where the first stitch is at xCoord 3,
     * otherwise, start at xCoord 1.
     *
     * @param yCoord
     */
    private void stitchRows(int yCoord) {
        System.out.println("Stitching at row " + yCoord);
        int startX = (yCoord % 4 == 0) ? 3 : 1;
        for (int xOffset = startX; xOffset < xAddresses - 1; xOffset = xOffset + 4) {
            SouthIn north = (SouthIn) nodes[yCoord + 1][xOffset];
            NorthIn south = (NorthIn) nodes[yCoord - 1][xOffset];
            assert north != null;
            assert south != null;

            placeNEdSJunction(north, south, xOffset, yCoord);
        }
    }

    private EastIn placeNEdSJunction(SouthIn north, NorthIn south, int xCoord, int yCoord) {
        boolean isRepeater = false;
        for (Address a : repeaterAddresses) {
            if (a.equals(xCoord, yCoord)) {
                isRepeater = true;
            }
        }

        if (isRepeater) {
            System.out.println(String.format("Repeater at (%d,%d)", xCoord, yCoord));
            NSRepeater node = new NSRepeater(new Address(xCoord, yCoord), executor);
            addJunction(node);
            stitchNS(north, node);
            stitchNS(node, south);
            return node;
        }
        else {
            NEdSJunction node = new NEdSJunction(new Address(xCoord, yCoord), executor);
            addJunction(node);
            stitchNS(north, node);
            stitchNS(node, south);

            WDestination dest = new WDestination(new Address(xCoord, yCoord), tracePackets, simulationRunning, executor);
            destinations.add(dest);
            stitchWE(node, dest);
            return node;
        }
    }

    /**
     * Ensure that all the paths to the south or north are terminated by loopback nodes
     *
     * @param yCoord
     */
    private void closeEndRow(int yCoord) {
        System.out.println("Closing end row");
        int startX = 3;
        for (int xOffset = startX; xOffset < xAddresses - 1; xOffset = xOffset + 4) {
            Locatable junctionNode = nodes[yCoord][xOffset];
            assert junctionNode != null;

            NorthIn south;
            SouthIn north;

            if (nodes[yCoord][xOffset].hasNorth()) {
                System.out.println("Found north-facing junction");
                south = (NorthIn) nodes[yCoord][xOffset];

                assert nodes[yCoord + 1][xOffset] == null;
                System.out.println("north is empty");

                north = new SLoopback(new Address(xOffset, yCoord + 1), executor);
                System.out.println("adding loopback at address" + north.getAddress().asString());
                addJunction(north);
            }
            else {
                System.out.println("Found south-facing junction");
                north = (SouthIn) nodes[yCoord][xOffset];

                assert nodes[yCoord - 1][xOffset] == null;
                System.out.println("south is empty");

                south = new NLoopback(new Address(xOffset, yCoord - 1), executor);
                System.out.println("adding loopback at address" + south.getAddress().asString());
                addJunction(south);
            }

            stitchNS(north, south);
        }
    }

    public static void stitchNS(SouthIn north, NorthIn south) {
        north.attachSouth(south);
        south.attachNorth(north);
    }

    public static void stitchWE(EastIn west, WestIn east) {
        west.attachEast(east);
        east.attachWest(west);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Locatable[][] getNodes() {
        return nodes;
    }
}
