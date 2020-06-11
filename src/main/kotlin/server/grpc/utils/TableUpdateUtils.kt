package server.grpc.utils

import core.handflow.hand.HandAction
import core.handflow.hand.HandState
import poker.proto.TableUpdate
import server.grpc.converters.toProtoTable

object TableUpdateUtils {
    fun tableUpdate(handState: HandState, handHistory: List<HandAction>, seatsNumber: Int): TableUpdate {
        return TableUpdate.newBuilder()
                .setTable(handState.toProtoTable(seatsNumber))
                .build()

        // todo: build hand history
    }
}
