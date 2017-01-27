package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Packet;

// Defines a junction that can take tokens FROM the NorthIn
public interface NorthIn extends Locatable {
    public void fromNorthThru(Packet packet);
    public void fromNorthTurn(Packet packet);
}
