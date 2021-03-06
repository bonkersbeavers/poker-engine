package core.handflow.positions

import core.handflow.hand.ApplicableHandAction
import core.handflow.hand.HandAction
import core.handflow.hand.HandState
import core.handflow.player.getBySeat
import core.handflow.player.next
import core.handflow.player.prev

object ShiftPositions: HandAction(), ApplicableHandAction {

    override fun apply(handState: HandState): HandState {
        fun previousPosition(from: Int): Int = if (from > 0) (from - 1) else (handState.seatsNumber - 1)

        val players = handState.players
        val currentPositions = handState.positions

        // Big blind always moves to the next player.
        val newBigBlindSeat = players.next(currentPositions.bigBlind).seat

        // In heads up game, button and small blind always point to the other player.
        if (players.count() == 2) {
            val otherPlayerSeat = players.next(newBigBlindSeat).seat
            val newPositions = Positions(
                    button = otherPlayerSeat,
                    smallBlind = otherPlayerSeat,
                    bigBlind = newBigBlindSeat
            )
            return handState.copy(positions = newPositions)
        }

        // When there are at least three players:

        // 1) new small blind position is determined first
        val newSmallBlindSeat = when (players.getBySeat(currentPositions.bigBlind)) {

            // If previous big blind player left the game, small blind will point to an empty seat
            // and should be placed right before the new big blind.
            null -> previousPosition(newBigBlindSeat)

            // Otherwise small blind always takes the previous big blind's position.
            else -> currentPositions.bigBlind
        }

        // 2) button position is determined at the end
        val newButtonSeat = when (players.next(currentPositions.button).seat) {

            // If moving the button to the next player results in button pointing to the same seat
            // as small blind, the button should be placed right before the small blind.
            newSmallBlindSeat -> previousPosition(newSmallBlindSeat)

            // This can happen only when new small blind is dead (points to an empty seat).
            // In such case the button should also be placed right before the small blind.
            newBigBlindSeat -> previousPosition(newSmallBlindSeat)

            // In any other case the button should point to the player that is seated right before
            // the small blind.
            else -> players.prev(newSmallBlindSeat).seat
        }

        val newPositions = Positions(
                newButtonSeat,
                newSmallBlindSeat,
                newBigBlindSeat
        )
        return handState.copy(positions = newPositions)
    }
}
