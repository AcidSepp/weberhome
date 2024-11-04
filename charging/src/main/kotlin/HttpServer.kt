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
import java.io.File

class HttpServer(private val state: State, private val port: Int = 8888, private val host: String = "0.0.0.0") {
    fun start() {
        val static = File("static")
        embeddedServer(Netty, port = port, host = host) {
            install(ContentNegotiation) {
                json()
            }
            routing {
                staticFiles("/", static) {
                    default("src/dist/static/index.html")
                }
                get("/state") {
                    call.respond(StateResponse(state.solarOverProductionCharging.get()))
                }
                put("/state") {
                    val requestBody = call.receive<StateResponse>()
                    state.solarOverProductionCharging.set(requestBody.solarOverProductionCharging)
                    if (requestBody.solarOverProductionCharging) {
                        Logger.info("Enabled solar overproduction charging.")
                    } else {
                        Logger.info("Disabled solar overproduction charging.")
                    }
                }
            }
        }.start(wait = false)
        Logger.info("HttpServer is listening on '$host:$port'")
    }
}

@Serializable
data class StateResponse(var solarOverProductionCharging: Boolean)
