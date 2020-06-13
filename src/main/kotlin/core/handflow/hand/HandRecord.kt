package core.handflow.hand

class HandRecord(initState: HandState) {
    private var handHistory: MutableList<HandAction> = mutableListOf()
    private var state: HandState = initState

    fun register(action: HandAction) = handHistory.add(action)

    fun registerSequence(actions: Iterable<HandAction>) = handHistory.addAll(actions)

    fun getHandHistory() = handHistory.toList()

    fun resolveHandState() = handHistory.fold(initial = state, operation = { state: HandState, action: HandAction ->
        if (action is ApplicableHandAction) action.apply(state) else state
    })

    fun squash() {
        state = resolveHandState()
        handHistory = mutableListOf()
    }
}
