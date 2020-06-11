package server.grpc

import core.handflow.blinds.Blinds

data class CashGameTableSettings(
        val seatsNumber: Int,
        val defaultStack: Int,
        val blinds: Blinds,
        val playerActionTime: Int,
        val dealerActionTime: Int,
        val newHandTime: Int
)
