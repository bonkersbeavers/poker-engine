package server.grpc.converters

import core.handflow.betting.*
import core.handflow.hand.manager.ActionType
import poker.proto.*

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

fun BettingActionType?.toProtoActionLog(): BettingActionLog {
    return when (this) {
        null -> BettingActionLog.newBuilder().setNoAction(NoActionLog.getDefaultInstance()).build()
        BettingActionType.POST -> BettingActionLog.newBuilder().setPost(PostLog.getDefaultInstance()).build()
        BettingActionType.FOLD -> BettingActionLog.newBuilder().setFold(FoldLog.getDefaultInstance()).build()
        BettingActionType.CHECK -> BettingActionLog.newBuilder().setCheck(CheckLog.getDefaultInstance()).build()
        BettingActionType.CALL -> BettingActionLog.newBuilder().setCall(CallLog.getDefaultInstance()).build()
        BettingActionType.BET -> BettingActionLog.newBuilder().setBet(BetLog.getDefaultInstance()).build()
        BettingActionType.RAISE -> BettingActionLog.newBuilder().setRaise(RaiseLog.getDefaultInstance()).build()
    }
}
