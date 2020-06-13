package core.handflow.positions

import core.handflow.hand.ApplicableHandAction
import core.handflow.hand.HandAction
import core.handflow.hand.HandState
import core.handflow.player.next

object DrawPositions: HandAction(), ApplicableHandAction {

    override fun apply(handState: HandState): HandState {
        val takenSeats = handState.players.map { it.seat }
        val button = takenSeats.shuffled().first()

        val smallBlind = when (takenSeats.count()) {
            2 -> button
            else -> handState.players.next(button).seat
        }

        val bigBlind = handState.players.next(smallBlind).seat

        return handState.copy(positions = Positions(button, smallBlind, bigBlind))
    }
}
