package core.handflow.pot

import core.handflow.hand.ApplicableHandAction
import core.handflow.hand.HandAction
import core.handflow.hand.HandState

sealed class PotAction(open val playerSeat: Int, open val chips: Int) : HandAction(), ApplicableHandAction {
    override fun apply(handState: HandState): HandState {
        val newPlayers = handState.players.map {
            if (it.seat == playerSeat) it.copy(stack = it.stack + chips) else it
        }
        return handState.copy(players = newPlayers)
    }
}

//data class ReturnChips(
//        override val playerSeat: Int,
//        override val chips: Int
//) : PotAction(playerSeat, chips)

data class AwardChips(
        override val playerSeat: Int,
        override val chips: Int,
        val potNumber: Int
) : PotAction(playerSeat, chips)
