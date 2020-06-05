package core.handflow.betting

import core.handflow.hand.ActionValidation
import core.handflow.hand.HandState
import core.handflow.hand.ValidAction

data class Fold(override val seat: Int): InteractiveBettingAction(seat) {
    override fun apply(handState: HandState): HandState {
        val newPlayerStates = handState.players.map {
            if (it.seat == seat) it.copy(currentActionType = BettingActionType.FOLD) else it
        }
        return handState.copy(players = newPlayerStates)
    }

    override fun innerValidate(handState: HandState): ActionValidation = ValidAction
}
