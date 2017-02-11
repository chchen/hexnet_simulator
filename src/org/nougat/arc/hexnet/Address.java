package org.nougat.arc.hexnet;

public class Address {
    private final int xCoord;
    private final int yCoord;

    public Address(int x, int y) {
        xCoord = x;
        yCoord = y;
    }

    public String asString() {
        return String.format("(%d, %d)", xCoord, yCoord);
    }

    public int getYCoord() {
        return yCoord;
    }

    public int getXCoord() {
        return xCoord;
    }

    public boolean equals(int x, int y) {
        return (xCoord == x && yCoord == y);
    }

    public boolean equals(Address other) {
        return other.xCoord == this.xCoord && other.yCoord == this.yCoord;
    }

    public boolean xEqual(Address other) {
        return this.xCoord == other.xCoord;
    }

    public boolean xGreaterThan(Address other) {
        return this.xCoord > other.xCoord;
    }

    public boolean xLessThan(Address other) {
        return this.xCoord < other.xCoord;
    }

    public boolean yEqual(Address other) {
        return this.yCoord == other.yCoord;
    }

    public boolean yGreaterThan(Address other) {
        return this.yCoord > other.yCoord;
    }

    public boolean yLessThan(Address other) {
        return this.yCoord < other.yCoord;
    }
}
