package server.grpc.converters

import core.handflow.betting.*
import poker.proto.BettingActionRequest

fun BettingActionRequest.toInteractiveBettingAction(seat: Int): InteractiveBettingAction {
    return when {
        hasFold() -> Fold(seat = seat)
        hasCheck() -> Check(seat = seat)
        hasCall() -> Call(seat = seat)
        hasBet() -> Bet(seat = seat, chips = this.bet.chips)
        hasRaise() -> Raise(seat = seat, chips = this.raise.chips)
        else -> throw ServiceDataConverterException("BettingActionRequest to InteractiveBettingAction failed")
    }
}
