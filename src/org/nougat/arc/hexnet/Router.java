package org.nougat.arc.hexnet;

public interface Router {
    public void route(Packet packet);
}

final class RouterFactory {
    static Router northRouter(SouthIn next) {
        if (next.destinationIsNorth() || next.destinationIsWest() || next.destinationIsEast()) {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.equals(next.getAddress())) {
                        next.fromSouthTurn(packet);
                    } else {
                        next.fromSouthThru(packet);
                    }
                }
            };
        }
        else if (next.hasWest() && next.hasEast()) {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.xLessThan(next.getAddress())) {
                        next.fromSouthTurn(packet);
                    }
                    else {
                        next.fromSouthThru(packet);
                    }
                }
            };
        }
        else {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    next.fromSouthThru(packet);
                }
            };
        }
    }

    static Router southRouter(NorthIn next) {
        if (next.destinationIsSouth() || next.destinationIsWest() || next.destinationIsEast()) {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.equals(next.getAddress())) {
                        next.fromNorthTurn(packet);
                    } else {
                        next.fromNorthThru(packet);
                    }
                }
            };
        }
        else if (next.hasWest() && next.hasEast()) {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.xLessThan(next.getAddress())) {
                        next.fromNorthThru(packet);
                    }
                    else {
                        next.fromNorthTurn(packet);
                    }
                }
            };
        } else {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    next.fromNorthThru(packet);
                }
            };
        }
    }

    static Router westRouter(EastIn next) {
        if (next.destinationIsNorth() || next.destinationIsSouth() || next.destinationIsWest()) {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.equals(next.getAddress())) {
                        next.fromEastTurn(packet);
                    }
                    else {
                        next.fromEastThru(packet);
                    }
                }
            };
        }
        else if (next.hasNorth() && next.hasSouth()){
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.yLessThan(next.getAddress())) {
                        next.fromEastTurn(packet);
                    }
                    else {
                        next.fromEastThru(packet);
                    }
                }
            };
        }
        else if (!next.hasNorth() && next.hasSouth()) {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.yLessThan(next.getAddress())) {
                        next.fromEastTurn(packet);
                    }
                    else {
                        next.fromEastThru(packet);
                    }
                }
            };
        }
        else if (next.hasNorth() && !next.hasSouth()) {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.yGreaterThan(next.getAddress())) {
                        next.fromEastTurn(packet);
                    }
                    else {
                        next.fromEastThru(packet);
                    }
                }
            };
        }
        else {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    next.fromEastThru(packet);
                }
            };
        }
    }

    static Router eastRouter(WestIn next) {
        if (next.destinationIsNorth() || next.destinationIsSouth() || next.destinationIsEast()) {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.equals(next.getAddress())) {
                        next.fromWestTurn(packet);
                    }
                    else {
                        next.fromWestThru(packet);
                    }
                }
            };
        }
        else if (next.hasNorth() && next.hasSouth()){
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.yLessThan(next.getAddress())) {
                        next.fromWestTurn(packet);
                    }
                    else {
                        next.fromWestThru(packet);
                    }
                }
            };
        }
        else if (!next.hasNorth() && next.hasSouth()) {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.yLessThan(next.getAddress())) {
                        next.fromWestTurn(packet);
                    }
                    else {
                        next.fromWestThru(packet);
                    }
                }
            };
        }
        else if (next.hasNorth() && !next.hasSouth()) {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    if (packet.destination.yGreaterThan(next.getAddress())) {
                        next.fromWestTurn(packet);
                    }
                    else {
                        next.fromWestThru(packet);
                    }
                }
            };
        }
        else {
            return new Router() {
                @Override
                public void route(Packet packet) {
                    next.fromWestThru(packet);
                }
            };
        }
    }
}
