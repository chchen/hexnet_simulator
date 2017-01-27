package org.nougat.arc.hexnet.junction;

import org.nougat.arc.hexnet.Packet;

// Defines a junction that can take tokens FROM the EastIn
public interface EastIn extends Locatable {
    public void fromEastThru(Packet packet);
    public void fromEastTurn(Packet packet);
}