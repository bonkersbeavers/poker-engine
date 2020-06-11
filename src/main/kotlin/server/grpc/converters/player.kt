package server.grpc.converters

import core.handflow.player.Player

fun Player.toProtoPlayer(): poker.proto.Player {
    var builder = poker.proto.Player.newBuilder()
            .setSeat(this.seat)
            .setStack(this.stack)
            .setBet(this.currentBet)
            .setActionLog(this.currentActionType.toProtoActionLog())

    if (this.cards != null) {
        builder = builder.setHoleCards(0, this.cards.first.toProtoCard())
                .setHoleCards(1, this.cards.second.toProtoCard())

    }

    return builder.build()
}
