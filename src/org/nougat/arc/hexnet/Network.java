package org.nougat.arc.hexnet;

import org.nougat.arc.hexnet.destination.SDestination;
import org.nougat.arc.hexnet.junction.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A packet-switched network that has a
 */
public class Network {
    private Locatable[][] nodes;

    private final int rows;
    private final int columns;

    private final int xAddresses;
    private final int yAddresses;

    private List<Locatable> destinations;
    private List<Locatable> junctions;

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

    private void addJunction(Locatable newJunction) {
        junctions.add(newJunction);
        nodes[newJunction.getAddress().getYCoord()][newJunction.getAddress().getxCoord()] = newJunction;
    }

    /**
     * Lay a row of junctions (and associated destinations) where the brick corners face north
     */
    private void rowUp(int yCoord) {
        EastIn last;
        ELoopback node0 = new ELoopback(new Address(0, yCoord));
        addJunction(node0);
        NWEJunction node1 = new NWEJunction(new Address(1, yCoord));
        addJunction(node1);
        stitchWE(node0, node1);
        last = node1;
        for (int xOffset = 2; xOffset < xAddresses; xOffset = xOffset + 4) {
            NdNWESJunction node2 = new NdNWESJunction(new Address(xOffset, yCoord));
            addJunction(node2);
            stitchWE(last, node2);

            SDestination dest2 = new SDestination(new Address(xOffset, yCoord));
            destinations.add(dest2);
            stitchNS(dest2, node2);

            WESJunction node3 = new WESJunction(new Address(xOffset + 1, yCoord));
            addJunction(node3);
            stitchWE(node2, node3);

            NdSWENJunction node4 = new NdSWENJunction(new Address(xOffset + 2, yCoord));
            addJunction(node4);
            stitchWE(node3, node4);

            SDestination dest4 = new SDestination(new Address(xOffset + 2, yCoord));
            destinations.add(dest4);
            stitchNS(dest4, node4);

            NWEJunction node5 = new NWEJunction(new Address(xOffset + 3, yCoord));
            addJunction(node5);
            stitchWE(node4, node5);

            last = node5;
        }
        WLoopback node6 = new WLoopback(new Address(last.getAddress().getxCoord() + 1, yCoord));
        addJunction(node6);
        stitchWE(last, node6);
    }

    /**
     * Lay a row of junctions (and associated destinations) where the brick corners face south
     */
    private void rowDown(int yCoord) {
        EastIn last;
        ELoopback node0 = new ELoopback(new Address(0, yCoord));
        addJunction(node0);
        WESJunction node1 = new WESJunction(new Address(1, yCoord));
        addJunction(node1);
        stitchWE(node0, node1);
        last = node1;
        for (int xOffset = 2; xOffset < xAddresses; xOffset = xOffset + 4) {
            NdSWENJunction node2 = new NdSWENJunction(new Address(xOffset, yCoord));
            addJunction(node2);
            stitchWE(last, node2);

            SDestination dest2 = new SDestination(new Address(xOffset, yCoord));
            destinations.add(dest2);
            stitchNS(dest2, node2);

            NWEJunction node3 = new NWEJunction(new Address(xOffset + 1, yCoord));
            addJunction(node3);
            stitchWE(node2, node3);

            NdNWESJunction node4 = new NdNWESJunction(new Address(xOffset + 2, yCoord));
            addJunction(node4);
            stitchWE(node3, node4);

            SDestination dest4 = new SDestination(new Address(xOffset + 2, yCoord));
            destinations.add(dest4);
            stitchNS(dest4, node4);

            WESJunction node5 = new WESJunction(new Address(xOffset + 3, yCoord));
            addJunction(node5);
            stitchWE(node4, node5);

            last = node5;
        }
        WestIn node6 = new WLoopback(new Address(last.getAddress().getxCoord() + 1, yCoord));
        addJunction(node6);
        stitchWE(last, node6);
    }

    /**
     * Stitches two rows together.
     *
     * Rule: if the yCoord is divisible by 4, then it's a "narrow" stitch where the first stitch is at xCoord 3,
     * otherwise, start at xCoord 1.
     *
     * @param yCoord
     */
    private void stitchRows(int yCoord) {}


    public Network(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        xAddresses = 1 + (4 * columns) + 2;
        yAddresses = 1 + (2 * rows) + 2;

        nodes = new Locatable[yAddresses][xAddresses];

        destinations = new ArrayList<>();
        junctions = new ArrayList<>();

    }

    /*
    private List<List<Locatable>> rows = new ArrayList<>();


    private void generateRow() {
        int width = 1;

        List<Locatable> bottomRow = new ArrayList<>();
        List<Locatable> middleRow = new ArrayList<>();
        List<Locatable> topRow = new ArrayList<>();

        //bottomrow
        EastIn last = new ELoopback(new Address(0, 1));
        bottomRow.add(last);
        for (int col = 0; col < width; col++) {
            NWEJunction node10 = new NWEJunction(new Address(col + 1, 0));
            stitchWE(last, node10);
            NdNWESJunction node20 = new NdNWESJunction(new Address(col + 2, 0));
            stitchWE(node10, node20);
            WESJunction node30 = new WESJunction(new Address(col + 3, 0));
            stitchWE(node20, node30);
            NdSWENJunction node40 = new NdSWENJunction(new Address(col + 3, y 0));


        }
    }
    */

    public static void stitchNS(SouthIn north, NorthIn south) {
        north.attachSouth(south);
        south.attachNorth(north);
    }

    public static void stitchWE(EastIn west, WestIn east) {
        west.attachEast(east);
        east.attachWest(west);
    }

}
