package core.handflow.hand.manager

import core.handflow.hand.HandRecord

interface HandPhaseManager
interface AutoRunnablePhaseManager: HandPhaseManager {
    fun run()
    fun nextPhaseManager(): HandPhaseManager
}

interface InteractivePhaseManager: HandPhaseManager {
    fun
}

class
