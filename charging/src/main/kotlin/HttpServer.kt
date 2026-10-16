@file:OptIn(ExperimentalUuidApi::class)

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.tinylog.kotlin.Logger
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

class HttpServer(
    val charging: Charging,
    val port: Int = 8888,
    private val host: String = "0.0.0.0",
) {

    fun start() {
        embeddedServer(
            Netty,
            port = port,
            host = host
        ) {
            install(ContentNegotiation) {
                json()
            }
            routing {
                staticResources(
                    "/",
                    "static"
                ) {
                    default("index.html")
                }

                get("/state") {
                    call.respond(
                        charging.getState()
                    )
                }

                put("/overproductingCharging") {
                    val requestBody =
                        call.receive<OverproductionChargingRequest>()
                    charging.overProductionCharging =
                        requestBody.solarOverProductionCharging
                }

                post("/sessions") {
                    val session = call.receive<Session>()

                    if (session.start < Clock.System.now()) {
                        call.respond(HttpStatusCode.BadRequest, "'start' must not be in the past.")
                    }

                    if (session.stop < Clock.System.now()) {
                        call.respond(HttpStatusCode.BadRequest, "'stop' must not be in the past.")
                    }

                    if (session.stop < session.start) {
                        call.respond(HttpStatusCode.BadRequest, "'start' must happen before 'stop'.")
                    }

                    val uuid =
                        charging.scheduleSession(session)
                    call.respond(HttpStatusCode.Created, "/sessions/$uuid")
                }
            }
        }.start(wait = false)
        Logger.info("HttpServer is listening on '$host:$port'")
    }
}

@Serializable
data class OverproductionChargingRequest(
    var solarOverProductionCharging: Boolean,
)

@Serializable
data class Session(val start: Instant, val stop: Instant)