package server.grpc.converters

import core.handflow.betting.option.*

fun BettingActionOption.toProtoBettingActionOption(): poker.proto.BettingActionOption {
    return when (this) {
        is FoldOption -> poker.proto.BettingActionOption.newBuilder()
                .setFoldOption(poker.proto.FoldOption.getDefaultInstance()).build()

        is CheckOption -> poker.proto.BettingActionOption.newBuilder()
                .setCheckOption(poker.proto.CheckOption.getDefaultInstance()).build()

        is CallOption -> poker.proto.BettingActionOption.newBuilder()
                .setCallOption(poker.proto.CallOption.getDefaultInstance()).build()

        is BetOption -> poker.proto.BettingActionOption.newBuilder()
                .setBetOption(poker.proto.BetOption.newBuilder().setMinBet(this.minBet)).build()

        is RaiseOption -> poker.proto.BettingActionOption.newBuilder()
                .setRaiseOption(poker.proto.RaiseOption.newBuilder().setMinRaise(this.minRaise)).build()
    }
}
