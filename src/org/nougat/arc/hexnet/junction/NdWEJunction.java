package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Address;

import java.util.concurrent.ExecutorService;

public class NdWEJunction extends NWEJunction {
    public NdWEJunction(Address address, ExecutorService executor) {
        super(address, executor);
    }

    @Override
    public boolean destinationIsNorth() {
        return true;
    }

    @Override
    public String getLabel() {
        return "WE dest N";
    }
}
