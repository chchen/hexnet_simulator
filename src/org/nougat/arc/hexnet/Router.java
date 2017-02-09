package org.nougat.arc.hexnet;

import org.nougat.arc.hexnet.Packet;

public interface Router {
    public void route(Packet packet);
}
