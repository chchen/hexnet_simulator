package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Packet;

import java.util.concurrent.ExecutorService;

/**
 * This is a junction that has junctions to the west and east, and a destination to the north.
 * The junction to the west branches south, the destination to the east branches north.
 */
public class NdSWENJunction extends NWEJunction {
    public NdSWENJunction(Address address, ExecutorService executor) {
        super(address, executor);
    }

    @Override
    protected void sendWest(Packet packet) {
        packet.markPath(getAddress());
        if (packet.destination.yLessThan(west.getAddress())) {
            west.fromEastTurn(packet);
        }
        else {
            west.fromEastThru(packet);
        }
        executor.submit(sendWestTask);
    }

    @Override
    protected void sendEast(Packet packet) {
        packet.markPath(getAddress());
        if (packet.destination.yGreaterThan(east.getAddress())) {
            east.fromWestTurn(packet);
        }
        else {
            east.fromWestThru(packet);
        }
        executor.submit(sendEastTask);
    }

    @Override
    public String getLabel() {
        return "SWEN dest N";
    }
}
