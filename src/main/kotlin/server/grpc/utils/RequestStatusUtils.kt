package server.grpc.utils

import poker.proto.RequestStatus
import poker.proto.StatusCode

object RequestStatusUtils {
    fun ok(): RequestStatus {
        return RequestStatus.newBuilder()
                .setCode(StatusCode.OK)
                .build()
    }

    fun failed(message: String): RequestStatus {
        return RequestStatus.newBuilder()
                .setCode(StatusCode.FAILED)
                .setMessage(message)
                .build()
    }
}
