package org.nougat.arc.hexnet;

import org.nougat.arc.hexnet.Address;

public interface Locatable {
    public Address getAddress();
    public boolean hasNorth();
    public boolean hasSouth();
    public boolean hasWest();
    public boolean hasEast();
    public String getLabel();
}
