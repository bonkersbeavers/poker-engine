package core.handflow.player.management

import core.handflow.hand.*
import core.handflow.player.Player
import core.handflow.player.PlayerAction
import core.handflow.player.getBySeat

data class AddPlayer(val name: String, override val seat: Int, val stack: Int, val isNewPlayer: Boolean): PlayerManagementAction(seat) {
    override fun apply(handState: HandState): HandState {
        val newPlayer = Player(name = name, seat = seat, stack = stack)

        return if (isNewPlayer) {
            handState.copy(
                    players = handState.players + listOf(newPlayer),
                    newPlayersSeats = handState.newPlayersSeats.toSet() + seat
            )
        } else {
            handState.copy(
                    players = handState.players + listOf(newPlayer)
            )
        }
    }

    override fun validate(handState: HandState): ActionValidation {
        val occupiedSeats = handState.players.map { it.seat }
        if (seat < 0 || seat >= handState.seatsNumber)
            return InvalidAction("seat $seat not in table's valid seats range: 0 - ${handState.seatsNumber}")

        if (seat in occupiedSeats)
            return InvalidAction("seat $seat is already occupied by another player")

        return ValidAction
    }
}
