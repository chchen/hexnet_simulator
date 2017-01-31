package org.nougat.arc.hexnet;

// Defines a junction that can take tokens FROM the NorthIn
public interface NorthIn extends Locatable {
    public void fromNorthThru(Packet packet);
    public void fromNorthTurn(Packet packet);

    public void attachNorth(SouthIn north);
}
