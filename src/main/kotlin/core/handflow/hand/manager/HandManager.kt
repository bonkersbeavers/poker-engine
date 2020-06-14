package core.handflow.hand.manager

import core.handflow.HandFlowException
import core.handflow.betting.InteractiveBettingAction
import core.handflow.blinds.getBlindsPostActionsSequence
import core.handflow.dealer.Dealer
import core.handflow.hand.*
import core.handflow.helpers.CollectBets
import core.handflow.helpers.InitializeHand
import core.handflow.player.management.PlayerManagementAction
import core.handflow.positions.DrawPositions
import core.handflow.positions.ShiftPositions
import core.handflow.pot.getPotActionsSequence
import core.handflow.showdown.getShowdownActionsSequence

private enum class HandPhase {
    NONE,
    INIT,
    DEALER,
    BETTING_ROUND,
    BETTING_ROUND_CLEANUP,
    SHOWDOWN,
    ALL_IN_DUEL,
    POT_DISTRIBUTION,
    FINISHED
}

enum class ActionType {
    NO_ACTION,
    PLAYER_ACTION,
    DEALER_ACTION,
    NEW_HAND_ACTION
}

class HandManager(initState: HandState) {
    private val dealer = Dealer()

    private val handRecord: HandRecord = HandRecord(initState)
    private var currentPhase: HandPhase = HandPhase.NONE

    fun getHandState(): HandState = handRecord.resolveHandState()
    fun getHandHistory(): List<HandAction> = handRecord.getHandHistory()

    fun getNextActionType(): ActionType {
        return when (currentPhase) {
            HandPhase.NONE -> ActionType.NO_ACTION
            HandPhase.FINISHED -> ActionType.NEW_HAND_ACTION
            HandPhase.BETTING_ROUND -> ActionType.PLAYER_ACTION
            HandPhase.ALL_IN_DUEL -> ActionType.DEALER_ACTION
            else -> throw HandFlowException("HandManager found in illegal state with currentPhase `$currentPhase`")
        }
    }

    fun newHand(randomPositions: Boolean = false) {

        if (randomPositions) {
            handRecord.register(DrawPositions)
        }

        handRecord.squash()
        currentPhase = HandPhase.INIT
        autoRun()
    }

    // used only in interactive stage
    fun nextPlayerAction(action: InteractiveBettingAction): ActionValidation {

        if (currentPhase != HandPhase.BETTING_ROUND)
            throw HandFlowException("cannot apply player's action while not in betting round")

        val validation = action.validate(getHandState())
        if (validation is ValidAction) {
            handRecord.register(action)
            autoRun()
        }

        return validation
    }

    // used only in all is duel stage
    fun nextDealerAction() {
        if (currentPhase != HandPhase.ALL_IN_DUEL)
            throw HandFlowException("cannot trigger dealer action")

        handRecord.register(dealer.autoAction(getHandState()))
        autoRun()
    }

    fun managePlayers(action: PlayerManagementAction): ActionValidation {
        val validation = action.validate(getHandState())
        if (validation is ValidAction) {
            handRecord.register(action)
            autoRun()
        }

        return validation
    }

    private fun autoRun() {

        val updated = when (currentPhase) {
            HandPhase.NONE ->
                false

            HandPhase.INIT -> {
                dealer.shuffle()
                handRecord.register(InitializeHand)
                handRecord.register(ShiftPositions)
                // todo: collect ante
                handRecord.registerSequence(getBlindsPostActionsSequence(getHandState()))
                currentPhase = HandPhase.DEALER
                true
            }

            HandPhase.DEALER -> {
                handRecord.register(dealer.autoAction(getHandState()))
                currentPhase = HandPhase.BETTING_ROUND
                true
            }

            HandPhase.BETTING_ROUND -> {
                if (getHandState().activePlayer != null)
                    false
                else {
                    currentPhase = HandPhase.BETTING_ROUND_CLEANUP
                    true
                }
            }

            HandPhase.BETTING_ROUND_CLEANUP -> {
                handRecord.register(CollectBets)
                currentPhase = if (getHandState().handStage == HandStage.INTERACTIVE_STAGE) {
                    HandPhase.DEALER
                } else {
                    HandPhase.SHOWDOWN
                }
                true
            }

            HandPhase.SHOWDOWN -> {
                val showdownSequence = getShowdownActionsSequence(getHandState())
                handRecord.registerSequence(showdownSequence)

                currentPhase = if (getHandState().handStage == HandStage.ALLIN_DUEL_STAGE) {
                    HandPhase.ALL_IN_DUEL
                } else {
                    HandPhase.POT_DISTRIBUTION
                }
                true
            }

            HandPhase.ALL_IN_DUEL -> {
                if (getHandState().handStage == HandStage.RESULTS_STAGE) {
                    currentPhase = HandPhase.POT_DISTRIBUTION
                    true
                } else {
                    false
                }
            }

            HandPhase.POT_DISTRIBUTION -> {
                val potDistributionSequence = getPotActionsSequence(getHandState())
                handRecord.registerSequence(potDistributionSequence)
                currentPhase = HandPhase.FINISHED
                true
            }

            HandPhase.FINISHED -> {
                false
            }
        }

        if (updated)
            autoRun()
    }
}
