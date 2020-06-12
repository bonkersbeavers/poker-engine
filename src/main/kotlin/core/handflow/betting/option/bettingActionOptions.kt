package core.handflow.betting.option

sealed class BettingActionOption
object FoldOption: BettingActionOption()
object CheckOption: BettingActionOption()
object CallOption: BettingActionOption()
data class BetOption(val minBet: Int): BettingActionOption()
data class RaiseOption(val minRaise: Int): BettingActionOption()
