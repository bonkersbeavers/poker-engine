package server.grpc.converters

import core.handflow.hand.HandState
import poker.proto.Table

fun HandState.toProtoTable(revealSeats: Collection<Int>): Table {

    return Table.newBuilder()
            .setSeatsNumber(this.seatsNumber)
            .setPositions(this.positions.toProtoPositions())
            .setBlinds(this.blinds.toProtoBlinds())
            .addAllPlayers(this.players.map {
                val shouldRevealCards = it.seat in revealSeats
                it.toProtoPlayer(revealCards = shouldRevealCards)
            })
            .addAllCommunityCards(this.communityCards.map { it.toProtoCard() })
            .addAllPots(this.pots.map { it.size })
            .setActivePlayerSeat(this.activePlayer?.seat ?: -1)
            .build()
}
