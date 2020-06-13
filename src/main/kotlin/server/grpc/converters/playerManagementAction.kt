package server.grpc.converters

import core.handflow.player.management.AddPlayer
import core.handflow.player.management.RemovePlayer
import poker.proto.PlayerJoinRequest
import poker.proto.PlayerRemoveRequest

fun PlayerJoinRequest.toAddPlayer(stack: Int, isNewPlayer: Boolean): AddPlayer {
    return AddPlayer(name, seat, stack, isNewPlayer)
}

fun PlayerRemoveRequest.toRemovePlayer(): RemovePlayer {
    return RemovePlayer(seat)
}
