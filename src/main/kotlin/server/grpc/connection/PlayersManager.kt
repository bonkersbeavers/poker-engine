package server.grpc.connection

import core.handflow.hand.HandAction
import core.handflow.hand.HandState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import mu.KotlinLogging
import poker.proto.GameUpdate
import server.grpc.utils.GameUpdateUtils

private data class RegisteredPlayer(val seat: Int, val token: String)
private class RegisteredSubscription(val token: String, val channel: Channel<GameUpdate>)

private val logger = KotlinLogging.logger {}
class PlayersManager {
    private val players: MutableList<RegisteredPlayer> = mutableListOf()
    private val subscriptions: MutableList<RegisteredSubscription> = mutableListOf()

    fun registerPlayer(seat: Int, token: String) {
        players.add(RegisteredPlayer(seat, token))
    }

    fun addSubscription(token: String): ReceiveChannel<GameUpdate> {
        val channel = Channel<GameUpdate>(Channel.CONFLATED)
        this.subscriptions.add(RegisteredSubscription(token, channel))
        return channel
    }

    suspend fun update(handState: HandState, handHistory: List<HandAction>, actionToken: String?) {
        subscriptions.forEach { subscription ->
            val playerSeat = players.find { it.token == subscription.token }!!.seat
            val update = GameUpdateUtils.gameUpdate(handState, handHistory, actionToken, playerSeat)

            logger.debug("sending update to player on seat $playerSeat: $update")
            subscription.channel.send(update)
        }
    }
}
