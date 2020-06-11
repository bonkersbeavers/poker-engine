package server.grpc

import com.google.protobuf.Empty
import core.handflow.hand.HandState
import core.handflow.hand.InvalidAction
import core.handflow.hand.manager.ActionType
import core.handflow.hand.manager.HandManager
import core.handflow.positions.Positions
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.newSingleThreadContext
import mu.KotlinLogging
import poker.proto.*
import server.grpc.converters.toAddPlayer
import server.grpc.converters.toCashGameTableSettings
import server.grpc.converters.toInteractiveBettingAction
import server.grpc.converters.toRemovePlayer
import server.grpc.utils.RequestStatusUtils
import server.grpc.utils.TableUpdateUtils
import java.util.*
import kotlin.coroutines.CoroutineContext

private enum class ServiceState {
    CLEAR,
    INITIALIZED,
    RUNNING,
    STOPPED
}

private val logger = KotlinLogging.logger {}
class CashGameTableServiceImpl(override val coroutineContext: CoroutineContext = newSingleThreadContext("server-context")): CashGameTableServiceImplBase(coroutineContext) {
    private var state: ServiceState = ServiceState.CLEAR
    private var handManager: HandManager? = null
    private var settings: CashGameTableSettings? = null
    private val channel: Channel<TableUpdate> = Channel()
    private var actionToken: String? = null

    override suspend fun create(request: TableSettings): RequestStatus {
        logger.debug("received create request")

        if (state != ServiceState.CLEAR) {
            return RequestStatusUtils.failed("table already created")
        }

        settings = request.toCashGameTableSettings()
        val emptyState = HandState(
                players = emptyList(),
                blinds = settings!!.blinds,
                positions = Positions(-1, -1, -1)
        )
        handManager = HandManager(emptyState, settings!!.seatsNumber)
        update()

        state = ServiceState.INITIALIZED

        logger.debug("successfully created game environment")
        return RequestStatusUtils.ok()
    }

    override suspend fun start(request: Empty): RequestStatus {
        logger.debug("received start game request")

        if (state != ServiceState.INITIALIZED && state != ServiceState.STOPPED) {
            return RequestStatusUtils.failed("cannot start game when service state is $state")
        }

        if (handManager!!.getHandState().players.count() < 2) {
            return RequestStatusUtils.failed("not enough players to start the game")
        }

        handManager!!.newHand(randomPositions = true)
        update()

        state = ServiceState.RUNNING

        logger.debug("successfully started the game")
        return RequestStatusUtils.ok()
    }

    override suspend fun stop(request: Empty): RequestStatus {
        logger.debug("received stop game request")
        if (state != ServiceState.RUNNING) {
            return RequestStatusUtils.failed("cannot stop game when service state is $state")
        }

        state = ServiceState.STOPPED

        logger.debug("successfully stopped the game")
        return RequestStatusUtils.ok()
    }

    override fun subscribe(request: Empty): ReceiveChannel<TableUpdate> {
        logger.debug("received subscription request")
        return channel
    }

    override suspend fun addPlayer(request: PlayerJoinRequest): RequestStatus {
        logger.debug("received add player request")
        if (state == ServiceState.CLEAR) {
            return RequestStatusUtils.failed("cannot add player before the game is initialized")
        }

        val shouldJoinAsNewPlayer = state == ServiceState.RUNNING
        val stack = settings!!.defaultStack
        val action = request.toAddPlayer(stack, isNewPlayer = shouldJoinAsNewPlayer)

        val validation = action.validate(handManager!!.getHandState())

        if (validation is InvalidAction) {
            logger.debug("invalid add player request request: $action")
            return RequestStatusUtils.failed("invalid add player request: ${validation.reason}")
        }

        handManager!!.managePlayers(action)
        logger.debug("successfully added player: $action")
        update()
        return RequestStatusUtils.ok()
    }

    override suspend fun removePlayer(request: PlayerRemoveRequest): RequestStatus {
        logger.debug("received remove player request")
        if (state == ServiceState.CLEAR) {
            return RequestStatusUtils.failed("cannot remove player before the game is initialized")
        }

        val action = request.toRemovePlayer()
        val validation = action.validate(handManager!!.getHandState())

        if (validation is InvalidAction) {
            logger.debug("invalid remove player request request: $action")
            return RequestStatusUtils.failed("invalid remove player request: ${validation.reason}")
        }

        handManager!!.managePlayers(action)
        logger.debug("successfully removed player: $action")
        update()
        return RequestStatusUtils.ok()
    }

    override suspend fun takeAction(request: BettingActionRequest): RequestStatus {
        logger.debug("received action request")

        if (request.actionToken != actionToken) {
            logger.debug("invalid action token")
            return RequestStatusUtils.failed("received action token is invalid")
        }

        val state = handManager!!.getHandState()
        val activeSeat = state.activePlayer!!.seat
        val action = request.toInteractiveBettingAction(activeSeat)
        val validation = action.validate(state)

        if (validation is InvalidAction) {
            logger.debug("invalid action request: $action")
            return RequestStatusUtils.failed("invalid action: ${validation.reason}")
        }

        handManager!!.nextPlayerAction(action)
        logger.debug("successfully applied $action")
        update()
        return RequestStatusUtils.ok()
    }

    private suspend fun update() {
        actionToken = if (handManager!!.getNextActionType() == ActionType.PLAYER_ACTION)
            UUID.randomUUID().toString()
        else
            null

        val state = handManager!!.getHandState()
        val history = handManager!!.getHandHistory()
        val update = TableUpdateUtils.tableUpdate(state, history)

        channel.send(update)
    }
}
