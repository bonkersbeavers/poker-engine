package server.grpc

import com.google.protobuf.Empty
import core.handflow.HandFlowException
import core.handflow.hand.HandState
import core.handflow.hand.InvalidAction
import core.handflow.hand.manager.ActionType
import core.handflow.hand.manager.HandManager
import core.handflow.positions.Positions
import io.grpc.Status
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import mu.KotlinLogging
import poker.proto.*
import server.grpc.connection.PlayersManager
import server.grpc.converters.toAddPlayer
import server.grpc.converters.toCashGameTableSettings
import server.grpc.converters.toInteractiveBettingAction
import server.grpc.converters.toRemovePlayer
import server.grpc.utils.RequestStatusUtils
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
    private val playersManager: PlayersManager = PlayersManager()
    private var actionToken: String? = null

    override suspend fun reset(request: Empty): RequestStatus {
        logger.debug("reset request received")
        this.state = ServiceState.CLEAR
        this.handManager = null
        this.settings = null
        this.playersManager.clear()
        this.actionToken = null

        logger.debug("successfully cleared service's inner state")
        return RequestStatusUtils.ok()
    }

    override suspend fun create(request: TableSettings): RequestStatus {
        logger.debug("received create request: ${request.jsonSettings}")

        Channel.CONFLATED

        if (state != ServiceState.CLEAR) {
            logger.debug("table already created")
            return RequestStatusUtils.failed("table already created")
        }

        try {
            settings = request.toCashGameTableSettings()
            val emptyState = HandState(
                    seatsNumber = settings!!.seatsNumber,
                    players = emptyList(),
                    blinds = settings!!.blinds,
                    positions = Positions(-1, -1, -1)
            )
            handManager = HandManager(emptyState)
            update()

            state = ServiceState.INITIALIZED

            logger.debug("successfully created game environment")
            return RequestStatusUtils.ok()
        } catch (e: Exception) {  // todo: only json parse exceptions
            throw Status.UNKNOWN.withDescription(e.message).asRuntimeException()
        }
    }

    private suspend fun checkAndScheduleAutoPhase() {
        logger.debug("checking if auto hand phase should be scheduled")
        val actionType = handManager!!.getNextActionType()
        if (actionType == ActionType.DEALER_ACTION) {
            autoHandPhase(delayTime = settings!!.dealerActionTime.toLong())
        }

        else if (actionType == ActionType.NEW_HAND_ACTION) {
            autoHandPhase(delayTime = settings!!.newHandTime.toLong())
        }
    }

    private suspend fun autoHandPhase(delayTime: Long) {
        logger.debug("starting auto hand phase coroutine")
        delay(delayTime)
        val actionType = handManager!!.getNextActionType()

        when (actionType) {
            ActionType.DEALER_ACTION -> {
                handManager!!.nextDealerAction()
            }

            ActionType.NEW_HAND_ACTION -> {
                handManager!!.newHand()
            }

            else -> throw HandFlowException("attempt to autoRun hand manager when actionType is $actionType")
        }

        logger.debug("auto hand phase run successfully")
        update()

        checkAndScheduleAutoPhase()
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

    override fun subscribe(request: SubscriptionRequest): ReceiveChannel<GameUpdate> {
        logger.debug("received subscription request")

        return try {
            val subscriberChannel = playersManager.addSubscription(request.playerToken)
            val state = handManager!!.getHandState()
            val history = handManager!!.getHandHistory()

            launch {
                playersManager.updatePrivate(state, history, actionToken, request.playerToken)
            }

            logger.debug("added new subscriber")
            subscriberChannel

        } catch (e: TableServiceException) {
            throw Status.UNKNOWN.withDescription(e.message).asRuntimeException()
        }
    }

    override suspend fun addPlayer(request: PlayerJoinRequest): AddPlayerRequestStatus {
        logger.debug("received add player request: $request")
        if (state == ServiceState.CLEAR) {
            logger.debug("cannot add player before the game is created")
            return AddPlayerRequestStatus.newBuilder()
                    .setStatus(RequestStatusUtils.failed("cannot add player before the game is created"))
                    .build()
        }

        val shouldJoinAsNewPlayer = state == ServiceState.RUNNING
        val stack = settings!!.defaultStack
        val action = request.toAddPlayer(stack, isNewPlayer = shouldJoinAsNewPlayer)

        val validation = action.validate(handManager!!.getHandState())

        if (validation is InvalidAction) {
            logger.debug("invalid add player request: ${validation.reason}")
            return AddPlayerRequestStatus.newBuilder()
                    .setStatus(RequestStatusUtils.failed("invalid add player request: ${validation.reason}"))
                    .build()
        }

        val playerToken = UUID.randomUUID().toString()

        handManager!!.managePlayers(action)
        playersManager.registerPlayer(request.seat, playerToken)
        logger.debug("successfully added player: $action")
        update()
        return AddPlayerRequestStatus.newBuilder()
                .setStatus(RequestStatusUtils.ok())
                .setPlayerToken(playerToken)
                .build()
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
        checkAndScheduleAutoPhase()
        return RequestStatusUtils.ok()
    }

    // todo: token should not be changed if action didn't proceed
    private suspend fun update() {
        logger.debug("broadcasting updates")

        actionToken = if (handManager!!.getNextActionType() == ActionType.PLAYER_ACTION)
            UUID.randomUUID().toString()
        else
            null

        val state = handManager!!.getHandState()
        val history = handManager!!.getHandHistory()

        playersManager.update(state, history, actionToken)
        logger.debug("updates sent")
    }
}
