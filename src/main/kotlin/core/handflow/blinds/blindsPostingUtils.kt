package core.handflow.blinds

import core.handflow.hand.HandState
import core.handflow.betting.Post
import core.handflow.player.orderedBySeats

fun getBlindsPostActionsSequence(handState: HandState, newPlayersSeats: Collection<Int>): List<Post> {
    val actions = mutableListOf<Post>()
    val players = handState.players.orderedBySeats(handState.positions.smallBlind)

    for (player in players) {
        val chipsToPost = when (player.seat) {
            in newPlayersSeats -> handState.blinds.bigBlind
            handState.positions.bigBlind -> handState.blinds.bigBlind
            handState.positions.smallBlind -> handState.blinds.smallBlind
            else -> 0
        }

        if (chipsToPost > 0) {
            actions.add(Post(seat = player.seat, chips = chipsToPost))
        }
    }

    return actions
}
