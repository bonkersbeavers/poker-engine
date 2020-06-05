package core.handflow.player.management

import core.handflow.hand.ApplicableHandAction
import core.handflow.hand.ValidatableHandAction
import core.handflow.player.PlayerAction

abstract class PlayerManagementAction(override val seat: Int):
        PlayerAction(seat),
        ApplicableHandAction,
        ValidatableHandAction
