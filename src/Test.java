import org.nougat.arc.hexnet.*;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Test {
    static public void main(String[] args) {
        Queue<Packet> tracePackets = new ConcurrentLinkedQueue<>();
        Network n = new Network(tracePackets,3, 2, Arrays.asList(new Address(1,6), new Address(2, 7)));
        try {
            n.start();
            for (Locatable l : n.destinations) {
                Sender s = (Sender) l;
                for (Locatable d : n.destinations) {
                    s.sendPacket(42, d.getAddress());
                }
            }
        }
        catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                NetworkView view = new NetworkView(n, tracePackets);
            }
        });
    }
}
