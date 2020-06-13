package server.grpc.converters

import core.handflow.player.Player

fun Player.toProtoPlayer(): poker.proto.Player {
    var builder = poker.proto.Player.newBuilder()
            .setSeat(this.seat)
            .setStack(this.stack)
            .setBet(this.currentBet)
            .setActionLog(this.currentActionType.toProtoActionLog())

    if (this.cards != null) {
        builder = builder.addAllHoleCards(this.cards.toList().map { it.toProtoCard() })

    }

    return builder.build()
}
