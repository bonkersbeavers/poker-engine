package server.grpc.converters

import core.handflow.betting.option.resolveBettingOptions
import core.handflow.hand.HandState
import poker.proto.AvailableAction
import poker.proto.NextActionData
import poker.proto.NoAction
import poker.proto.Table

fun HandState.toProtoTable(seatsNumber: Int, actionToken: String?): Table {

    val nextActionData = when (actionToken) {
        null -> NextActionData.newBuilder()
                .setNoAction(NoAction.getDefaultInstance())
                .build()

        else -> NextActionData.newBuilder()
                .setAvailableAction(AvailableAction.newBuilder()
                        .setActivePlayerSeat(this.activePlayer!!.seat)
                        .setActionToken(actionToken)
                        .addAllActionOptions(this.resolveBettingOptions().map { it.toProtoBettingActionOption()} )
                        .build()
                ).build()
    }

    return Table.newBuilder()
            .setSeatsNumber(seatsNumber)
            .setPositions(this.positions.toProtoPositions())
            .setBlinds(this.blinds.toProtoBlinds())
            .addAllPlayers(this.players.map { it.toProtoPlayer() })
            .addAllCommunityCards(this.communityCards.map { it.toProtoCard() })
            .addAllPots(this.pots.map { it.size })
            .setNextAction(nextActionData)
            .build()
}
