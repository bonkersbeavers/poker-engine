package server.grpc.converters

import core.handflow.blinds.Blinds

fun Blinds.toProtoBlinds(): poker.proto.Blinds {
    return poker.proto.Blinds.newBuilder()
            .setAnte(this.ante)
            .setSmallBlind(this.smallBlind)
            .setBigBlind(this.bigBlind)
            .build()
}
