package service.grpc

import core.handflow.HandFlowException
import io.grpc.Status
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.util.concurrent.Executors.newFixedThreadPool

private val logger = KotlinLogging.logger {}
class SimpleHandManagementServiceImpl: SimpleHandManagementServiceImplBase(
        coroutineContext = newFixedThreadPool(1).asCoroutineDispatcher()
) {
    override suspend fun echo(request: SimpleMessage): SimpleMessage {
        try {
            logger.debug { "echo rpc called with contents '${request.contents}'" }
            throw HandFlowException("testing hand flow exception throwing")
        } catch (e: HandFlowException) {
//            throw Status.UNKNOWN.withDescription(e.message).asException()
            val e = Exception()
            throw Status.fromThrowable(e).asException()
        }
    }
}
