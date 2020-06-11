package server.grpc.utils

import core.handflow.hand.HandAction
import core.handflow.hand.HandState
import poker.proto.TableUpdate

object TableUpdateUtils {
    fun tableUpdate(handState: HandState, handHistory: List<HandAction>): TableUpdate {
        return TableUpdate.newBuilder().build() // todo
    }
}
