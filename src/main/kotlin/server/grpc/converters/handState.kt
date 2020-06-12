package server.grpc.converters

import core.handflow.betting.option.resolveBettingOptions
import core.handflow.hand.HandState
import poker.proto.Table

fun HandState.toProtoTable(seatsNumber: Int): Table {
    var tableBuilder = Table.newBuilder()
            .setSeatsNumber(seatsNumber)
            .setPositions(this.positions.toProtoPositions())
            .setBlinds(this.blinds.toProtoBlinds())

    this.players.forEachIndexed { index, player ->
        tableBuilder = tableBuilder.setPlayers(index, player.toProtoPlayer())
    }

    this.communityCards.forEachIndexed { index, card ->
        tableBuilder = tableBuilder.setCommunityCards(index, card.toProtoCard())
    }

    this.pots.forEachIndexed { index, pot ->
        tableBuilder = tableBuilder.setPots(index, pot.size)
    }

    val activePlayerSeat = this.activePlayer?.seat ?: -1
    tableBuilder = tableBuilder
            .setActivePlayerSeat(activePlayerSeat)

    this.resolveBettingOptions().forEachIndexed { index, bettingActionOption ->
        tableBuilder = tableBuilder.setActionOptions(index, bettingActionOption.toProtoBettingActionOption())
    }

    return tableBuilder.build()
}

//message Table {
//    int32 seatsNumber = 1;
//    Positions positions = 3;
//    Blinds blinds = 4;
//
//    repeated Player players = 2;
//    repeated Card communityCards = 5;
//    repeated int32 pots = 6;
//
//    int32 activePlayerSeat = 7;
//    repeated BettingActionOption actionOptions = 8;
//}
