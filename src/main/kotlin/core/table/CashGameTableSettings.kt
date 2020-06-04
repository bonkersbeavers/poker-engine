package core.table

import service.grpc.Blinds

data class CashGameTableSettings(
    val seatsNumber: Int,
    val decisionTimeout: Int? = null,
    val blinds: Blinds
)
