package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Packet;

// Defines a junction that can take tokens FROM the WestIn
public interface WestIn extends Locatable {
    public void fromWestThru(Packet packet);
    public void fromWestTurn(Packet packet);
}