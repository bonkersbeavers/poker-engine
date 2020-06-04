import core.handflow.blinds.Blinds
import core.handflow.hand.HandState
import core.handflow.player.Player
import core.handflow.positions.Positions
import core.table.CashGameTableSettings
import io.grpc.netty.NettyServerBuilder
import mu.KotlinLogging
import service.grpc.SimpleHandManagementServiceImpl
import service.local.LocalConsoleAdapter
import service.local.LocalHandManager
import java.net.InetSocketAddress

private val logger = KotlinLogging.logger {}
fun main(args: Array<String>) {
    val arg = args.getOrNull(0)
    when (arg) {
        "local-game" -> localGame()
        else -> grpcServer()
    }
}

fun localGame() {

    val adapter = LocalConsoleAdapter()

    val settings = CashGameTableSettings(4)
    val blinds = Blinds(50, 100)
    val positions = Positions(0, 1, 2)
    val players = (0 until 3).map { Player(seat = it, stack = 1000) }

    val manager = LocalHandManager(settings, adapter)
    var state = HandState(players, positions, blinds)

    while (true) {
        state = manager.playHand(state)
    }
}

fun grpcServer() {
    val handManagementService = SimpleHandManagementServiceImpl()
    val server = NettyServerBuilder
            .forAddress(InetSocketAddress("0.0.0.0", 8080))
            .addService(handManagementService)
            .build()

    Runtime.getRuntime().addShutdownHook(Thread {
        logger.warn { "JVM shutdown" }

        server.shutdown()
        server.awaitTermination()

        logger.warn( "gRPC server terminated")
    })

    server.start()
    logger.info { "gRPC server started" }
    server.awaitTermination()
}
