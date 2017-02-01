import org.nougat.arc.hexnet.*;
import org.nougat.arc.hexnet.destination.SDestination;
import org.nougat.arc.hexnet.destination.WDestination;
import org.nougat.arc.hexnet.junction.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Test {
    public static void stitchNS(SouthIn north, NorthIn south) {
        System.out.println(String.format("Stitching NS %s and %s", north.getAddress().asString(), south.getAddress().asString()));
        north.attachSouth(south);
        south.attachNorth(north);
    }

    public static void stitchWE(EastIn west, WestIn east) {
        System.out.println(String.format("Stitching WE %s and %s", west.getAddress().asString(), east.getAddress().asString()));
        west.attachEast(east);
        east.attachWest(west);
    }

    private NWEJunction node00 = new NWEJunction(new Address(0, 0));
    private NdNWESJunction node10 = new NdNWESJunction(new Address(1, 0));
    private WESJunction node20 = new WESJunction(new Address(2, 0));
    private NdSWENJunction node30 = new NdSWENJunction(new Address(3, 0));
    private NWEJunction node40 = new NWEJunction(new Address(4, 0));

    private NEdSJunction node01 = new NEdSJunction(new Address(0, 1));
    private NEdSJunction node41 = new NEdSJunction(new Address(4, 1));

    private WESJunction node02 = new WESJunction(new Address(0,2));
    private NdSWENJunction node12 = new NdSWENJunction(new Address(1, 2));
    private NWEJunction node22 = new NWEJunction(new Address(2,2));
    private NdNWESJunction node32 = new NdNWESJunction(new Address(3, 2));
    private WESJunction node42 = new WESJunction(new Address(4,2));
    private NdSWENJunction node52 = new NdSWENJunction(new Address(5, 2));
    private NWEJunction node62 = new NWEJunction(new Address(6, 2));

    private NEdSJunction node23 = new NEdSJunction(new Address(2, 3));
    private NEdSJunction node63 = new NEdSJunction(new Address(6, 3));

    private WESJunction node24 = new WESJunction(new Address(2,4));
    private NdSWENJunction node34 = new NdSWENJunction(new Address(3,4));
    private NWEJunction node44 = new NWEJunction(new Address(4,4));
    private NdNWESJunction node54 = new NdNWESJunction(new Address(5,4));
    private WESJunction node64 = new WESJunction(new Address(6,4));

    private ELoopback loop_10 = new ELoopback(new Address(-1, 0));
    private ELoopback loop_12 = new ELoopback(new Address(-1, 2));
    private NLoopback loop2_1 = new NLoopback(new Address(2, -1));
    private WLoopback loop50 = new WLoopback(new Address(5, 0));
    private WLoopback loop72 = new WLoopback(new Address(7, 2));
    private ELoopback loop14 = new ELoopback(new Address(1, 4));
    private SLoopback loop45 = new SLoopback(new Address(4,5));
    private WLoopback loop74 = new WLoopback(new Address(7, 4));

    private SDestination dest10 = new SDestination(new Address(1, 0));
    private SDestination dest30 = new SDestination(new Address(3, 0));
    private WDestination dest01 = new WDestination(new Address(0,1));
    private WDestination dest41 = new WDestination(new Address(4,1));
    private SDestination dest12 = new SDestination(new Address(1,2));
    private SDestination dest32 = new SDestination(new Address(3,2));
    private SDestination dest52 = new SDestination(new Address(5,2));
    private WDestination dest23 = new WDestination(new Address(2,3));
    private WDestination dest63 = new WDestination(new Address(6,3));
    private SDestination dest34 = new SDestination(new Address(3,4));
    private SDestination dest54 = new SDestination(new Address(5,4));


    Random random = new Random();

    List<Thread> junctions = Arrays.asList(node00, node10, node20, node30, node40, node01, node41, node02, node12, node22, node32, node42, node52, node62, node23, node63, node24, node34, node44, node54, node64, loop_10, loop_12, loop2_1, loop50, loop72, loop14, loop45, loop74);
    List<Thread> destinations = Arrays.asList(dest10, dest30, dest01, dest41, dest12, dest32, dest52, dest23, dest63, dest34, dest54);

    public void hookup() {
        // Terminate loose ends
        stitchWE(loop_10, node00);
        stitchWE(loop_12, node02);
        stitchNS(node20, loop2_1);
        stitchWE(node40, loop50);
        stitchWE(node62, loop72);
        stitchWE(loop14, node24);
        stitchNS(loop45, node44);
        stitchWE(node64, loop74);

        // Destinations
        stitchNS(dest10, node10);
        stitchNS(dest30, node30);
        stitchWE(node01, dest01);
        stitchWE(node41, dest41);
        stitchNS(dest12, node12);
        stitchNS(dest32, node32);
        stitchNS(dest52, node52);
        stitchWE(node23, dest23);
        stitchWE(node63, dest63);
        stitchNS(dest34, node34);
        stitchNS(dest54, node54);

        // Interjunction links
        stitchWE(node00, node10);
        stitchWE(node10, node20);
        stitchWE(node20, node30);
        stitchWE(node30, node40);
        stitchNS(node01, node00);
        stitchNS(node41, node40);
        stitchNS(node02, node01);
        stitchNS(node42, node41);
        stitchWE(node02, node12);
        stitchWE(node12, node22);
        stitchWE(node22, node32);
        stitchWE(node32, node42);
        stitchWE(node42, node52);
        stitchWE(node52, node62);
        stitchNS(node23, node22);
        stitchNS(node63, node62);
        stitchNS(node24, node23);
        stitchNS(node64, node63);
        stitchWE(node24, node34);
        stitchWE(node34, node44);
        stitchWE(node44, node54);
        stitchWE(node54, node64);
    }

    public void start() throws InterruptedException {
        for (Thread i : junctions) {
            i.start();
        }
        for (Thread i : destinations) {
            i.start();
        }
        for (Thread tsrc : destinations) {
            Sender src = (Sender) tsrc;
            for (Thread tdst : destinations ) {
                Locatable dst = (Locatable) tdst;
                src.sendPacket(random.nextInt(), dst.getAddress());
            }
            Thread.sleep(1000);
        }
    }

    static public void main(String[] args) {
        Test t = new Test();
        t.hookup();
        try {
            t.start();
        }
        catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

}
