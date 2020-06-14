import io.grpc.netty.NettyServerBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import server.grpc.CashGameTableServiceImpl
import java.net.InetSocketAddress

private val logger = KotlinLogging.logger {}
fun main(args: Array<String>) {

    localGame()
    val arg = args.getOrNull(0)
    when (arg) {
        "local-game" -> localGame()
        else -> grpcServer()
    }
}

fun localGame() {

//    val adapter = LocalConsoleAdapter()
//
//    val blinds = Blinds(5, 10)
//    val positions = Positions(0, 1, 2)
//
//    val controller = LocalGameController(3, adapter)
//    val players = (0 until 3).map { Player(seat = it, stack = 1000) }
//    var state = HandState(players, positions, blinds)
//
//    while (true) {
//        state = controller.playHand(state)
//    }


//    val settings = CashGameTableSettings(4, blinds = blinds)
//
//    val players = (0 until 3).map { Player(seat = it, stack = 1000) }
//
//    val manager = LocalHandManager(settings, adapter)
//    var state = HandState(players, positions, blinds)
//
//    while (true) {
//        state = manager.playHand(state)
//    }
}

fun grpcServer() {
    val handManagementService = CashGameTableServiceImpl()
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
