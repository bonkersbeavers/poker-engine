package core.handflow.betting

import core.handflow.hand.*
import core.handflow.player.PlayerAction

abstract class InteractiveBettingAction(override val seat: Int): BettingAction(seat) {
    override fun validate(handState: HandState): ActionValidation {
        if (handState.activePlayer == null)
            return InvalidAction("no active player in current state")

        val activeSeat = handState.activePlayer.seat
        if (activeSeat != seat)
            return InvalidAction("active player's seat $activeSeat is different than action's target seat $seat")

        return innerValidate(handState)
    }

    abstract fun innerValidate(handState: HandState): ActionValidation
}
