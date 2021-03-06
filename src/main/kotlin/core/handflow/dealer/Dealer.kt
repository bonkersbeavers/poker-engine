package core.handflow.dealer

import core.cards.Card
import core.cards.baseDeck
import core.handflow.hand.HandState
import core.handflow.player.Player
import core.handflow.HandFlowException

class Dealer {
    private lateinit var deckIterator: ListIterator<Card>

    fun shuffle() {
        deckIterator = baseDeck.shuffled().listIterator()
    }

    fun autoAction(handState: HandState): DealerAction {
        return when(handState.bettingRound) {
            null -> getDealHoleCardsAction(handState.players)
            BettingRound.PRE_FLOP -> getDealFlopAction()
            BettingRound.FLOP -> getDealTurnAction()
            BettingRound.TURN -> getDealRiverAction()
            BettingRound.RIVER -> throw HandFlowException("cannot deal any more cards when on river")
        }
    }

    private fun getDealHoleCardsAction(players: List<Player>): DealHoleCards {
        val seatToCardsMapping = HashMap<Int, Pair<Card, Card>>()

        for (player in players) {
            val cards = Pair(deckIterator.next(), deckIterator.next())
            seatToCardsMapping[player.seat] = cards
        }

        return DealHoleCards(seatToCardsMapping)
    }

    private fun getDealFlopAction(): DealFlop {
        val cards = Triple(deckIterator.next(), deckIterator.next(), deckIterator.next())
        return DealFlop(cards)
    }

    private fun getDealTurnAction(): DealTurn = DealTurn(deckIterator.next())

    private fun getDealRiverAction(): DealRiver = DealRiver(deckIterator.next())
}
