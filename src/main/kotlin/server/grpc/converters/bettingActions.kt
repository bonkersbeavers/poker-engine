package server.grpc.converters

import core.handflow.betting.*
import poker.proto.*

fun BettingActionRequest.toInteractiveBettingAction(seat: Int): InteractiveBettingAction {
    return when {
        hasFoldAction() -> Fold(seat = seat)
        hasCheckAction() -> Check(seat = seat)
        hasCallAction() -> Call(seat = seat)
        hasBetAction() -> Bet(seat = seat, chips = this.betAction.chips)
        hasRaiseAction() -> Raise(seat = seat, chips = this.raiseAction.chips)
        else -> throw ServiceDataConverterException("BettingActionRequest to InteractiveBettingAction failed")
    }
}

fun BettingActionType?.toProtoActionLog(): BettingActionLog {
    return when (this) {
        null -> BettingActionLog.newBuilder().setNoAction(NoActionLog.getDefaultInstance()).build()
        BettingActionType.POST -> BettingActionLog.newBuilder().setPostAction(PostLog.getDefaultInstance()).build()
        BettingActionType.FOLD -> BettingActionLog.newBuilder().setFoldAction(FoldLog.getDefaultInstance()).build()
        BettingActionType.CHECK -> BettingActionLog.newBuilder().setCheckAction(CheckLog.getDefaultInstance()).build()
        BettingActionType.CALL -> BettingActionLog.newBuilder().setCallAction(CallLog.getDefaultInstance()).build()
        BettingActionType.BET -> BettingActionLog.newBuilder().setBetAction(BetLog.getDefaultInstance()).build()
        BettingActionType.RAISE -> BettingActionLog.newBuilder().setRaiseAction(RaiseLog.getDefaultInstance()).build()
    }
}
