package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Packet;

/**
 * This is a junction that has junctions to the west and east, and a destination to the north.
 * The junction to the west branches south, the destination to the east branches north.
 */
public class NdSWENJunction extends NWEJunction {
    public NdSWENJunction(Address address) {
        super(address);
    }

    @Override
    protected boolean sendWest() {
        Packet nextPacket = toWest.poll();
        if (nextPacket == null) {
            return false;
        }
        nextPacket.markPath(getAddress());
        if (nextPacket.destination.yLessThan(west.getAddress())) {
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
        nextPacket.markPath(getAddress());
        if (nextPacket.destination.yGreaterThan(east.getAddress())) {
            east.fromWestTurn(nextPacket);
        }
        else {
            east.fromWestThru(nextPacket);
        }
        return true;
    }

    @Override
    public String getLabel() {
        return "SWEN dest N";
    }
}
