package server.grpc.utils

import core.handflow.betting.option.resolveBettingOptions
import core.handflow.hand.HandAction
import core.handflow.hand.HandState
import core.handflow.showdown.ShowCards
import poker.proto.*
import server.grpc.converters.toProtoBettingActionOption
import server.grpc.converters.toProtoTable

object GameUpdateUtils {

    fun gameUpdate(
            handState: HandState,
            handHistory: List<HandAction>,
            actionToken: String?,
            forSeat: Int?
    ): GameUpdate {

        val activePlayerSeat = handState.activePlayer?.seat

        val nextActionData = when {
            (forSeat != null && forSeat == activePlayerSeat) -> NextActionData.newBuilder()
                    .setAvailableAction(AvailableAction.newBuilder()
                            .setActionToken(actionToken)
                            .addAllActionOptions(handState.resolveBettingOptions().map { it.toProtoBettingActionOption()} )
                            .build()
                    ).build()

            else -> NextActionData.newBuilder()
                    .setNoAction(NoAction.getDefaultInstance())
                    .build()
        }

        val revealSeats = handHistory.filterIsInstance<ShowCards>().map { it.seat }.toMutableList()
        if (forSeat != null)
            revealSeats.add(forSeat)

        return GameUpdate.newBuilder()
                .setTable(handState.toProtoTable(revealSeats))
                .setNextAction(nextActionData)
                .build()

        // todo: build hand history
    }
}
