package core.handflow.positions

import core.handflow.hand.HandState
import core.handflow.player.next

fun HandState.withRandomPositions(seatsNumber: Int): HandState {
    val takenSeats = players.map { it.seat }
    val button = takenSeats.shuffled().first()

    val smallBlind = when (takenSeats.count()) {
        2 -> button
        else -> players.next(button).seat
    }

    val bigBlind = players.next(smallBlind).seat

    return this.copy(positions = Positions(button, smallBlind, bigBlind))
}
