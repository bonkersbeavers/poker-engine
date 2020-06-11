package server.grpc.converters

import core.handflow.positions.Positions

fun Positions.toProtoPositions(): poker.proto.Positions {
    return poker.proto.Positions.newBuilder()
            .setButton(this.button)
            .setSmallBlind(this.smallBlind)
            .setBigBlind(this.bigBlind)
            .build()
}
