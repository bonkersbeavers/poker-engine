package server.grpc.converters

import core.cards.Card
import core.cards.CardRank
import core.cards.CardSuit


fun Card.toProtoCard(): poker.proto.Card {
    return poker.proto.Card.newBuilder()
            .setRank(this.rank.toProtoRank())
            .setSuit(this.suit.toProtoSuit())
            .build()
}

fun Card.toProtoHoleCard(reveal: Boolean): poker.proto.HoleCard {
    return if (reveal) {
        poker.proto.HoleCard.newBuilder()
                .setRevealedCard(this.toProtoCard())
                .build()
    }
    else
        poker.proto.HoleCard.newBuilder()
                .setHiddenCard(poker.proto.HiddenCard.getDefaultInstance())
                .build()
}

private fun CardRank.toProtoRank(): poker.proto.Rank {
    return when (this) {
        CardRank.ACE -> poker.proto.Rank.ACE
        CardRank.TWO -> poker.proto.Rank.TWO
        CardRank.THREE -> poker.proto.Rank.THREE
        CardRank.FOUR -> poker.proto.Rank.FOUR
        CardRank.FIVE -> poker.proto.Rank.FIVE
        CardRank.SIX -> poker.proto.Rank.SIX
        CardRank.SEVEN -> poker.proto.Rank.SEVEN
        CardRank.EIGHT -> poker.proto.Rank.EIGHT
        CardRank.NINE -> poker.proto.Rank.NINE
        CardRank.TEN -> poker.proto.Rank.TEN
        CardRank.JACK -> poker.proto.Rank.JACK
        CardRank.QUEEN -> poker.proto.Rank.QUEEN
        CardRank.KING -> poker.proto.Rank.KING
    }
}

private fun CardSuit.toProtoSuit(): poker.proto.Suit {
    return when (this) {
        CardSuit.HEARTS -> poker.proto.Suit.HEARTS
        CardSuit.SPADES -> poker.proto.Suit.SPADES
        CardSuit.CLUBS -> poker.proto.Suit.CLUBS
        CardSuit.DIAMONDS -> poker.proto.Suit.DIAMONDS
    }
}
