package server.grpc.converters

import core.handflow.player.Player

fun Player.toProtoPlayer(revealCards: Boolean): poker.proto.Player {
    var builder = poker.proto.Player.newBuilder()
            .setName(this.name)
            .setSeat(this.seat)
            .setStack(this.stack)
            .setBet(this.currentBet)
            .setActionLog(this.currentActionType.toProtoActionLog())

    if (this.cards != null) {
        builder = builder.addAllHoleCards(this.cards.toList().map { it.toProtoHoleCard(revealCards) })

    }

    return builder.build()
}
