package server.local
//
//import core.handflow.HandFlowException
//import core.handflow.hand.HandStage
//import core.handflow.hand.HandState
//import core.handflow.hand.InvalidAction
//import core.handflow.hand.manager.HandManager
//import core.handflow.pot.PotAction
//import core.handflow.showdown.ShowdownAction
//
//class LocalGameController(val seatsNumber: Int, val playerAdapter: LocalConsoleAdapter) {
//
//    private var handNumber: Int = 0
//
//    fun playHand(initialState: HandState): HandState {
//        handNumber++
//        println("===================================== HAND NUMBER $handNumber ".padEnd(87, padChar = '='))
//
//        val executor = HandManager(initialState, seatsNumber)
//        executor.start()
//
//        while (executor.getHandState().handStage != HandStage.RESULTS_STAGE) {
//            playerAdapter.update(executor.getHandState())
//
//            when (executor.getHandState().handStage) {
//                HandStage.INTERACTIVE_STAGE -> {
//                    val action = playerAdapter.requestBettingAction(executor.getHandState().activePlayer!!.seat)
//                    val validation = executor.nextPlayerAction(action)
//                    if (validation is InvalidAction)
//                        println(validation.reason)
//                }
//
//                HandStage.ALLIN_DUEL_STAGE -> {
//                    executor.nextAction()
//                }
//
//                else -> throw HandFlowException("whats the big fn deal")
//            }
//        }
//
//        // showdown phase
//        val showdownSequence = executor.getHandHistory().filterIsInstance<ShowdownAction>()
//        playerAdapter.printShowdown(showdownSequence)
//
//        val potDistributionSequence = executor.getHandHistory().filterIsInstance<PotAction>()
//        playerAdapter.printPotDistribution(potDistributionSequence)
//
//        println("\nfinal results: ")
//        playerAdapter.update(executor.getHandState())
//
//        return executor.getHandState()
//    }
//}
