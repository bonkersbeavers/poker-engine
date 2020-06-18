package server.grpc

class TableServiceException(override val message: String): Exception(message)
