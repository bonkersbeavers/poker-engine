package core.handflow.hand.manager

import core.handflow.HandFlowException
import core.handflow.blinds.getBlindsPostActionsSequence
import core.handflow.dealer.Dealer
import core.handflow.hand.HandAction
import core.handflow.hand.HandRecord
import core.handflow.hand.HandStage
import core.handflow.hand.HandState
import core.handflow.helpers.InitializeHand
import core.handflow.positions.ShiftPositions

class HandManager(val seatsNumber: Int) {
    var handNumber: Int = 0
        private set

    private var handRecord: HandRecord? = null
    private var dealer: Dealer? = null

    fun startHand(initialState: HandState) {
        handNumber += 1
        handRecord = HandRecord(initialState)
        dealer = Dealer()

        handRecord!!.register(InitializeHand)
        handRecord!!.register(ShiftPositions(seatsNumber))
        // TODO() collect ante
        handRecord!!.registerSequence(getBlindsPostActionsSequence(getCurrentState()))


    }

    fun getCurrentState(): HandState {
        if (handRecord == null) {
            throw HandFlowException("cannot resolve hand state, hand not started yet")
        }

        return handRecord!!.resolveHandState()
    }

    fun getHandHistory(): List<HandAction> {
        if (handRecord == null) {
            throw HandFlowException("cannot resolve hand history, hand not started yet")
        }

        return handRecord!!.getHandHistory()
    }

    private fun runAuto() {
        val handState = handRecord!!.resolveHandState()
        if (handState.activePlayer != null)
            return

        if (handState.handStage == HandStage.ALLIN_DUEL_STAGE)
            return


        runAuto()
    }
}
