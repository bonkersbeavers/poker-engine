package server.grpc

import com.google.protobuf.Empty
import core.handflow.hand.HandState
import core.handflow.hand.InvalidAction
import core.handflow.hand.manager.ActionType
import core.handflow.hand.manager.HandManager
import core.handflow.positions.Positions
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
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
    private val updateChannel: BroadcastChannel<TableUpdate> = ConflatedBroadcastChannel()
    private var actionToken: String? = null

    override suspend fun create(request: TableSettings): RequestStatus {
        logger.debug("received create request: ${request.jsonSettings}")

        if (state != ServiceState.CLEAR) {
            logger.debug("table already created")
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
            logger.debug("cannot start game when service state is $state")
            return RequestStatusUtils.failed("cannot start game when service state is $state")
        }

        if (handManager!!.getHandState().players.count() < 2) {
            logger.debug("not enough players to start the game")
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
            logger.debug("cannot stop game when service state is $state")
            return RequestStatusUtils.failed("cannot stop game when service state is $state")
        }

        state = ServiceState.STOPPED

        logger.debug("successfully stopped the game")
        return RequestStatusUtils.ok()
    }

    override fun subscribe(request: Empty): ReceiveChannel<TableUpdate> {
        logger.debug("received subscription request")
        val subscriberChannel = updateChannel.openSubscription()
        logger.debug("added new subscriber")
        return subscriberChannel
    }

    override suspend fun addPlayer(request: PlayerJoinRequest): RequestStatus {
        logger.debug("received add player request: $request")
        if (state == ServiceState.CLEAR) {
            logger.debug("cannot add player before the game is created")
            return RequestStatusUtils.failed("cannot add player before the game is created")
        }

        val shouldJoinAsNewPlayer = state == ServiceState.RUNNING
        val stack = settings!!.defaultStack
        val action = request.toAddPlayer(stack, isNewPlayer = shouldJoinAsNewPlayer)

        val validation = action.validate(handManager!!.getHandState())

        if (validation is InvalidAction) {
            logger.debug("invalid add player request: ${validation.reason}")
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
            logger.debug("received remove player request")
            return RequestStatusUtils.failed("cannot remove player before the game is initialized")
        }

        val action = request.toRemovePlayer()
        val validation = action.validate(handManager!!.getHandState())

        if (validation is InvalidAction) {
            logger.debug("invalid remove player request: $action")
            return RequestStatusUtils.failed("invalid remove player request: ${validation.reason}")
        }

        handManager!!.managePlayers(action)
        logger.debug("successfully removed player: $action")
        update()
        return RequestStatusUtils.ok()
    }

    override suspend fun takeAction(request: BettingActionRequest): RequestStatus {
        logger.debug("received action request: $request, token = ${request.actionToken}")

        if (request.actionToken != actionToken) {
            logger.debug("received action token is invalid")
            return RequestStatusUtils.failed("received action token is invalid")
        }

        val state = handManager!!.getHandState()
        val activeSeat = state.activePlayer!!.seat
        val action = request.toInteractiveBettingAction(activeSeat)
        val validation = action.validate(state)

        if (validation is InvalidAction) {
            logger.debug("invalid action request: $action")
            return RequestStatusUtils.failed("invalid action request: ${validation.reason}")
        }

        handManager!!.nextPlayerAction(action)
        logger.debug("successfully applied $action")
        update()
        return RequestStatusUtils.ok()
    }

    // todo: token should not be changed if action didn't proceed
    private suspend fun update() {
        logger.debug("sending table update to update channel with subscribers count: $updateChannel.")

        actionToken = if (handManager!!.getNextActionType() == ActionType.PLAYER_ACTION)
            UUID.randomUUID().toString()
        else
            null

        val state = handManager!!.getHandState()
        val history = handManager!!.getHandHistory()
        val update = TableUpdateUtils.tableUpdate(state, history, settings!!.seatsNumber, actionToken)

        updateChannel.send(update)
    }
}
