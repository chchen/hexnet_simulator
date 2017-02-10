package org.nougat.arc.hexnet;

// Defines a junction that can take tokens FROM the SouthIn
public interface SouthIn extends Locatable {
    public void fromSouthThru(Packet packet);
    public void fromSouthTurn(Packet packet);
    public void attachSouth(NorthIn south);
}
