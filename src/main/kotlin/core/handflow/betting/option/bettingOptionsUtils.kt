package core.handflow.betting.option

import core.handflow.betting.*
import core.handflow.hand.HandState
import core.handflow.hand.ValidAction
import kotlin.math.min

fun HandState.resolveBettingOptions(): List<BettingActionOption> {
    if (this.activePlayer == null)
        return emptyList()

    val optionsList = mutableListOf<BettingActionOption>()
    val activeSeat = activePlayer.seat

    if (Fold(activeSeat).validate(this) is ValidAction)
        optionsList.add(FoldOption)

    if (Check(activePlayer.seat).validate(this) is ValidAction)
        optionsList.add(CheckOption)

    if (Call(activeSeat).validate(this) is ValidAction)
        optionsList.add(CallOption)

    val possibleBet = min(activePlayer.maxBet, this.minRaise)

    if (Bet(activeSeat, possibleBet).validate(this) is ValidAction)
        optionsList.add(BetOption(possibleBet))

    if (Raise(activeSeat, possibleBet).validate(this) is ValidAction)
        optionsList.add(RaiseOption(possibleBet))

    return optionsList
}
