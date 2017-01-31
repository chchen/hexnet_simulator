package org.nougat.arc.hexnet;

import org.nougat.arc.hexnet.Address;
import org.nougat.arc.hexnet.Packet;

public interface Sender {
    public void sendPacket(int payload, Address destination);
}
