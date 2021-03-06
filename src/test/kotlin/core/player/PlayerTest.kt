package core.player
//
//import core.cards.Card
//import core.cards.CardRank
//import core.cards.CardSuit
//import core.hand.player.betting.ActionType
//import core.pokerhands.TwoPair
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.TestInstance
//import org.junit.jupiter.api.assertThrows
//import java.lang.AssertionError
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class PlayerTest {
//
//    @Test
//    fun `withBet method should properly transfer chips from player's stack to their bet`() {
//        val player = Player(
//                chipsInPot = 500,
//                seat = 0,
//                stack = 1000,
//                bet = 0
//        )
//
//        val player1 = player.withBet(500)
//        assert(player1.bet == 500)
//        assert(player1.stack == 500)
//
//        val player2 = player1.withBet(800)
//        assert(player2.bet == 800)
//        assert(player2.stack == 200)
//    }
//
//    @Test
//    fun `maxBet should properly calculate the maximum amount a player can bet`() {
//        val player = Player(
//                chipsInPot = 500,
//                seat = 0,
//                stack = 1000,
//                bet = 800
//        )
//
//        assert(player.maxBet == 1800)
//    }
//
//    @Test
//    fun `Player instantiation should fail if either of the stack, chipsInPot or bet is negative`() {
//        assertThrows<AssertionError> {
//            Player(
//                    chipsInPot = -40,
//                    seat = 0,
//                    stack = 50
//            )
//        }
//
//        assertThrows<AssertionError> {
//            Player(
//                    chipsInPot = 0,
//                    seat = 0,
//                    stack = -100
//            )
//        }
//
//        assertThrows<AssertionError> {
//            Player(
//                    chipsInPot = 100,
//                    seat = 0,
//                    stack = 50,
//                    bet = -5
//            )
//        }
//    }
//
//    @Test
//    fun `isInGame should check if player has folded`() {
//        val playerNotInGame = Player(
//                chipsInPot = 500,
//                seat = 0,
//                stack = 1000,
//                bet = 800,
//                lastAction = ActionType.FOLD
//        )
//        assert(!playerNotInGame.isInGame)
//
//        val playerInGame = Player(
//                chipsInPot = 500,
//                seat = 0,
//                stack = 1000,
//                bet = 800,
//                lastAction = ActionType.CALL
//        )
//        assert(playerInGame.isInGame)
//    }
//
//    @Test
//    fun `isAllIn should check if player hasn't folded and his stack is empty`() {
//        val playerAllIn = Player(
//                chipsInPot = 500,
//                seat = 0,
//                stack = 0,
//                bet = 800,
//                lastAction = ActionType.CALL
//        )
//        assert(playerAllIn.isAllIn)
//
//        val foldPlayer = Player(
//                chipsInPot = 500,
//                seat = 0,
//                stack = 0,
//                bet = 800,
//                lastAction = ActionType.FOLD
//        )
//        assert(!foldPlayer.isAllIn)
//
//        val betPlayer = Player(
//                chipsInPot = 500,
//                seat = 0,
//                stack = 200,
//                bet = 800,
//                lastAction = ActionType.BET
//        )
//        assert(!betPlayer.isAllIn)
//    }
//
//    @Test
//    fun `isDecisive should check if player is able to make any decision in game`() {
//        val playerAllIn = Player(
//                chipsInPot = 500,
//                seat = 0,
//                stack = 0,
//                bet = 800,
//                lastAction = ActionType.CALL
//        )
//        assert(!playerAllIn.isDecisive)
//
//        val foldPlayer = Player(
//                chipsInPot = 500,
//                seat = 0,
//                stack = 0,
//                bet = 800,
//                lastAction = ActionType.FOLD
//        )
//        assert(!foldPlayer.isDecisive)
//
//        val betPlayer = Player(
//                chipsInPot = 500,
//                seat = 0,
//                stack = 200,
//                bet = 800,
//                lastAction = ActionType.BET
//        )
//        assert(betPlayer.isDecisive)
//    }
//
//    @Test
//    fun `hand member should create the best poker hand out of hole cards and community cards`() {
//        val player = Player(
//                seat = 0,
//                stack = 0,
//                holeCards = listOf(
//                        Card(CardRank.ACE, CardSuit.SPADES),
//                        Card(CardRank.SIX, CardSuit.HEARTS)
//                )
//        )
//
//        val communityCards = listOf(
//                Card(CardRank.JACK, CardSuit.HEARTS),
//                Card(CardRank.TWO, CardSuit.CLUBS),
//                Card(CardRank.TWO, CardSuit.DIAMONDS),
//                Card(CardRank.SIX, CardSuit.CLUBS),
//                Card(CardRank.KING, CardSuit.DIAMONDS)
//        )
//
//        val properHandCards = setOf(
//                Card(CardRank.ACE, CardSuit.SPADES),
//                Card(CardRank.TWO, CardSuit.CLUBS),
//                Card(CardRank.TWO, CardSuit.DIAMONDS),
//                Card(CardRank.SIX, CardSuit.CLUBS),
//                Card(CardRank.SIX, CardSuit.HEARTS)
//        )
//
//        val playerHand = player.hand(communityCards)
//
//        assert(playerHand is TwoPair)
//        assert(playerHand.cards == properHandCards)
//    }
//
//    @Test
//    fun `moveBetToPot should move player's bet to pot`() {
//        val player = Player(
//                chipsInPot = 100,
//                seat = 0,
//                stack = 1000,
//                bet = 800,
//                lastAction = ActionType.BET
//        )
//
//        val playerAfterMovingBetsToPot = player.moveBetToPot()
//
//        assert(playerAfterMovingBetsToPot.bet == 0)
//        assert(playerAfterMovingBetsToPot.chipsInPot == 900)
//    }
//}
