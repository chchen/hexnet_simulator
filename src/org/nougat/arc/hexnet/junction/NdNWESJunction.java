package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Packet;

/**
 * This is a junction that has junctions to the west and east, and a destination to the north.
 * The junction to the west branches north, the destination to the east branches south.
 */
public class NdNWESJunction extends NWEJunction {
    @Override
    protected boolean sendWest() {
        Packet nextPacket = toWest.poll();
        if (nextPacket == null) {
            return false;
        }
        if (nextPacket.destination.yGreaterThan(west.getAddress())) {
            west.fromEastTurn(nextPacket);
        }
        else {
            west.fromEastThru(nextPacket);
        }
        return true;
    }

    @Override
    protected boolean sendEast() {
        Packet nextPacket = toEast.poll();
        if (nextPacket == null) {
            return false;
        }
        if (nextPacket.destination.yLessThan(east.getAddress())) {
            east.fromWestTurn(nextPacket);
        }
        else {
            east.fromWestThru(nextPacket);
        }
        return true;
    }
}
