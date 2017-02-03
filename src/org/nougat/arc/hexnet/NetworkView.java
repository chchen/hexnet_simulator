package org.nougat.arc.hexnet;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class NetworkView extends JFrame {
    private DrawCanvas canvas;

    private Network network;
    private Queue<Packet> tracePackets;

    private Timer redrawTimer;

    // Constructor to set up the GUI components and event handlers
    public NetworkView(Network network, Queue<Packet> tracePackets) {
        int rows = network.getRows();
        int columns = network.getColumns();
        this.network = network;
        this.tracePackets = tracePackets;

        int desiredWidth = 1200;
        int desiredHeight = 900;

        canvas = new DrawCanvas();    // Construct the drawing canvas
        canvas.setPreferredSize(new Dimension(desiredWidth, desiredHeight));

        // Set the Drawing JPanel as the JFrame's content-pane
        Container cp = getContentPane();
        cp.add(canvas);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setTitle("Hexnet");
        setVisible(true);

        redrawTimer = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tracePackets.peek() != null) {
                    repaint();
                }
            }
        });
        redrawTimer.start();
    }

    /**
     * Define inner class DrawCanvas, which is a JPanel used for custom drawing.
     */
    private class DrawCanvas extends JPanel {
        // Override paintComponent to perform your own painting
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.black);
            for (Locatable[] row : network.getNodes()) {
                for (Locatable node : row) {
                    if (node != null) {
                        drawNode(g2, node);
                    }
                }
            }
            Packet p = tracePackets.poll();
            if (p != null) {
                g2.setColor(Color.blue);
                g2.setStroke(new BasicStroke(5));
                drawPath(g2, p);
            }
        }
    }

    private static void drawPath(Graphics2D g, Packet packet) {
        g.drawString(
                String.format("Path from %s to %s", packet.source.asString(), packet.destination.asString()),
                10,
                10);
        int xLast = packet.source.getXCoord() * 100;
        int yLast = 800 - (packet.source.getYCoord() * 100);
        g.setColor(Color.green);
        g.drawRect(xLast, yLast, 10, 10);
        g.setColor(Color.blue);
        for (Address a : packet.getPath()) {
            int x = a.getXCoord() * 100;
            int y = 800 - (a.getYCoord() * 100);
            g.drawLine(xLast, yLast, x, y);
            xLast = x;
            yLast = y;
        }
        g.setColor(Color.red);
        g.drawRect(xLast, yLast, 20, 20);
        g.setColor(Color.blue);    }

    private static void drawNode(Graphics2D g, Locatable node) {
        int xOrigin = node.getAddress().getXCoord() * 100;
        int yOrigin = 800 - (node.getAddress().getYCoord() * 100);
        g.drawString(node.getAddress().asString(), xOrigin + 10, yOrigin + 20);
        if (node.hasNorth()) {
            g.drawLine(xOrigin, yOrigin, xOrigin, yOrigin - 50);
        }
        if (node.hasSouth()) {
            g.drawLine(xOrigin, yOrigin, xOrigin, yOrigin + 50);
        }
        if (node.hasWest()) {
            g.drawLine(xOrigin, yOrigin, xOrigin - 50, yOrigin);
        }
        if (node.hasEast()) {
            g.drawLine(xOrigin, yOrigin, xOrigin + 50, yOrigin);
        }
    }
}
