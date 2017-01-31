package org.nougat.arc.hexnet;

// Defines a junction that can take tokens FROM the EastIn
public interface EastIn extends Locatable {
    public void fromEastThru(Packet packet);
    public void fromEastTurn(Packet packet);

    public void attachEast(WestIn east);
}