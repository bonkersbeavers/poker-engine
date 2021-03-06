package core.handflow.betting

import core.handflow.hand.ActionValidation
import core.handflow.hand.HandState
import core.handflow.hand.InvalidAction
import core.handflow.hand.ValidAction
import core.handflow.player.getBySeat

data class Raise(override val seat: Int, val chips: Int): InteractiveBettingAction(seat) {
    override fun apply(handState: HandState): HandState {
        val newPlayerStates = handState.players.map {
            if (it.seat == seat) {
                it.withBet(chips).copy(currentActionType = BettingActionType.RAISE)
            }
            else it
        }
        val stateWithUpdatedPlayer = handState.copy(players = newPlayerStates)

        val raiseIsBigEnough = chips >= handState.minRaise
        val raisedAmount = chips - handState.lastLegalBet

        if (raiseIsBigEnough) {
            return stateWithUpdatedPlayer.copy(
                    lastLegalBet = chips,
                    minRaise = chips + raisedAmount,
                    extraBet = 0
            )
        } else {
            // The raise is player's all-in, but is not high enough to be considered a legal raise.
            return stateWithUpdatedPlayer.copy(
                    extraBet = raisedAmount
            )
        }
    }

    override fun innerValidate(handState: HandState): ActionValidation {
        val player = handState.players.getBySeat(seat)!!

        val playerIsAllowedToRaise = (player.currentBet < handState.lastLegalBet) or (player.currentActionType == BettingActionType.POST)
        val playerHasEnoughChips = player.maxBet >= chips
        val raiseIsHigherThanCurrentBet = chips > handState.totalBet
        val raiseIsHigherOrEqualToMinRaise = chips >= handState.minRaise
        val betHasBeenMade = handState.lastLegalBet > 0
        val raiseIsPlayersAllIn = chips == player.maxBet

        return when {
            playerIsAllowedToRaise.not() ->
                InvalidAction("cannot raise if betting action has not been restarted since player's previous play")

            playerHasEnoughChips.not() ->
                InvalidAction("raise of size $chips is higher than player's maximum possible bet ${player.maxBet}")

            betHasBeenMade.not() ->
                InvalidAction("cannot raise if no bet has been made")

            raiseIsHigherThanCurrentBet.not() ->
                InvalidAction("raise of $chips is not higher than current bet ${handState.totalBet}")

            raiseIsHigherOrEqualToMinRaise.not() and raiseIsPlayersAllIn.not() ->
                InvalidAction("raise of size $chips is smaller than minimum legal raise ${handState.minRaise}")

            else -> ValidAction
        }
    }
}
