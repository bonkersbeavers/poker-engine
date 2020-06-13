package core.handflow.player.management

import core.handflow.hand.*
import core.handflow.player.getBySeat

data class RemovePlayer(override val seat: Int): PlayerManagementAction(seat) {
    override fun apply(handState: HandState): HandState {
        val player = handState.players.getBySeat(seat)!!
        val potContribution = handState.seatToPotContribution.getOrDefault(seat, 0)

        val updatedContributions = handState.seatToPotContribution.toMutableMap()
        updatedContributions[player.seat] = potContribution + player.currentBet

        val newPlayers = handState.players.filter { it.seat != seat }

        return handState.copy(players = newPlayers, seatToPotContribution = updatedContributions.toMap())
    }

    override fun validate(handState: HandState): ActionValidation {
        val occupiedSeats = handState.players.map { it.seat }
        if (seat !in occupiedSeats)
            return InvalidAction("seat $seat is not occupied by any player")

        return ValidAction
    }
}
