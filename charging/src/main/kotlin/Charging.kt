import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.tinylog.kotlin.Logger
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.abs
import kotlin.math.sqrt

private const val FRONIUS_API_URL =
    "http://fronius-wechselrichter/solar_api/v1/GetPowerFlowRealtimeData.fcgi"
private const val WALLBOX_POWER_DRAW_URL = "http://warp2-296n/meter/values"
private const val WALLBOX_CURRENT_URL = "http://warp2-296n/evse/global_current"
private const val WALLBOX_CURRENT_UPDATE_URL =
    "http://warp2-296n/evse/global_current_update"
private const val WALLBOX_START_CHARGE_URL =
    "http://warp2-296n/evse/start_charging "
private const val WALLBOX_STOP_CHARGE_URL =
    "http://warp2-296n/evse/stop_charging"

private const val VARTA_BATTERIE = "varta-batterie"

private const val MIN_OVERPRODUCTION_MILLI_AMPS = 6000

fun Int.almostEquals(other: Int, delta: Int) = abs(this - other) < delta

fun main() {

    println(
        """
---
        _ _   _                                 _____ 
  /\/\ (_) |_| |_ ___ _ ____      _____  __ _  |___  |
 /    \| | __| __/ _ \ '__\ \ /\ / / _ \/ _` |    / / 
/ /\/\ \ | |_| ||  __/ |   \ V  V /  __/ (_| |   / /  
\/    \/_|\__|\__\___|_|    \_/\_/ \___|\__, |  /_/   
                                        |___/                  

Charging version 0.4.0
---
    """.trimIndent()
    )

    val state = State(AtomicBoolean(true))
    HttpServer(state).start()

    val master = ModbusTCPMaster(
        VARTA_BATTERIE,
        502
    )
    master.connect()

    val client = OkHttpClient.Builder()
        .build()

    Timer().scheduleAtFixedRate(
        0L,
        Duration.ofSeconds(30)
            .toMillis()
    ) {
        if (state.solarOverProductionCharging.get()) {
            val batteryChargingPowerWatts = master.readBatteryChargingPower()
            val gridPowerWatts = master.readGridPower()
            val solarPanelPowerWatts = client.readSolarPanelPower()
            val wallboxPowerWatts = client.readWallboxPower()

            val powerUsage =
                solarPanelPowerWatts - batteryChargingPowerWatts - gridPowerWatts
            val overProductionWatts =
                solarPanelPowerWatts - powerUsage + wallboxPowerWatts
            val nextChargingCurrentMilliAmps =
                powerToMilliAmps(overProductionWatts).coerceAtMost(15_000f).toInt()

            Logger.info(
                """
                Setting charge based on solar power:
                    - Grid Power: ${gridPowerWatts}W
                    - Battery charging power: ${batteryChargingPowerWatts}W
                    - Wallbox power: ${wallboxPowerWatts}W
                    - Solar panel power: ${solarPanelPowerWatts}W
                    - Power usage: ${powerUsage}W
                    - Overproduction: ${overProductionWatts}W
                    - Next Charging Power: ${overProductionWatts}W
                    - Next ChargingEnabled Current: ${nextChargingCurrentMilliAmps}mA
                """.trimIndent()
            )

            if (nextChargingCurrentMilliAmps < MIN_OVERPRODUCTION_MILLI_AMPS) {
                client.stopCharge()
            } else {
                client.startCharge()
                client.setGlobalCurrent(nextChargingCurrentMilliAmps)
            }
        } else {
            client.setGlobalCurrent(16_000)
        }
    }
}

private fun powerToMilliAmps(powerWatts: Int) =
    (powerWatts / (400f * sqrt(3f))) * 1000f

private fun OkHttpClient.setGlobalCurrent(desiredGlobalCurrent: Int) {
    Logger.info("Updating charge current to ${desiredGlobalCurrent}mA.")
    val jsonString = "{\"current\":${desiredGlobalCurrent}}"
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody: RequestBody = jsonString.toRequestBody(mediaType)

    try {
        val request = Request.Builder()
            .url(WALLBOX_CURRENT_UPDATE_URL)
            .post(requestBody)
            .build()
        val response = this.newCall(request)
            .execute()
        println(response.body.string())
        check(response.code == 200)
        val actualGlobalCurrent = this.getGlobalCurrent()

        if (desiredGlobalCurrent.almostEquals(actualGlobalCurrent, 50)) {
            Logger.error("Actual charge current (${actualGlobalCurrent}mA) does not match the desired current (${desiredGlobalCurrent}mA)")
        } else {
            Logger.info("Successfully updated charge current to ${desiredGlobalCurrent}mA.")
        }
    } catch (e: Exception) {
        Logger.warn(
            e,
            "Exception while changing wallbox current: "
        )
    }
}

private fun OkHttpClient.getGlobalCurrent() = try {
    Logger.info("Requesting charge current.")
    val request = Request.Builder()
        .url(WALLBOX_CURRENT_URL)
        .get()
        .build()
    val response = this.newCall(request)
        .execute()
    check(response.code == 200)
    val chargeCurrent =
        JsonParser.parseString(response.body.string()).asJsonObject.get("current").asInt
    Logger.info("Successfully requested charge current: ${chargeCurrent}mA.")
    chargeCurrent
} catch (e: Exception) {
    Logger.warn(
        e,
        "Exception requesting changing wallbox current: "
    )
    -1
}


private fun OkHttpClient.startCharge() = try {
    Logger.info("Starting Charging...")
    val jsonString = "{}"
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val request = Request.Builder()
        .url(WALLBOX_START_CHARGE_URL)
        .post(jsonString.toRequestBody(mediaType))
        .build()
    val response = newCall(request).execute()
    println(response.body.string())
    check(response.code == 200)
    Logger.info("Successfully Started Charging...")
} catch (e: Exception) {
    Logger.warn(
        e,
        "Exception while starting charging: "
    )
}

private fun OkHttpClient.stopCharge() = try {
    Logger.info("Stopping Charging...")
    val jsonString = "{}"
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val request = Request.Builder()
        .url(WALLBOX_STOP_CHARGE_URL)
        .post(jsonString.toRequestBody(mediaType))
        .build()
    val response = newCall(request).execute()
    println(response.body.string())
    check(response.code == 200)
    Logger.info("Successfully Stopped Charging!")
} catch (e: Exception) {
    Logger.warn(
        e,
        "Exception while stopping charging: "
    )
}

private fun ModbusTCPMaster.readBatteryChargingPower() = try {
    readMultipleRegisters(
        1066,
        1
    ).map {
        it.value.toShort()
    }
        .first()
        .toInt()
} catch (e: Exception) {
    Logger.warn(
        e,
        "Exception while fetching Modbus data: "
    )
    -1
}

private fun ModbusTCPMaster.readGridPower() = try {
    readMultipleRegisters(
        1078,
        1
    ).map {
        it.value.toShort()
    }
        .first()
        .toInt()
} catch (e: Exception) {
    Logger.warn(
        e,
        "Exception while fetching Modbus data from battery: "
    )
    -1
}


private fun OkHttpClient.readSolarPanelPower() = try {
    val request = Request.Builder()
        .url(FRONIUS_API_URL)
        .build()
    val body = newCall(request).execute().body
    JsonParser.parseString(body.string()).asJsonObject.get("Body").asJsonObject.get(
        "Data"
    ).asJsonObject.get("Inverters").asJsonObject.get("1").asJsonObject.get("P").asInt
} catch (e: Exception) {
    Logger.warn(
        e,
        "Exception while fetching HTTP data from inverter: "
    )
    -1
}

private fun OkHttpClient.readWallboxPower() = try {
    val request = Request.Builder()
        .url(WALLBOX_POWER_DRAW_URL)
        .build()
    val body = newCall(request).execute().body
    JsonParser.parseString(body.string()).asJsonObject.get("power").asInt
} catch (e: Exception) {
    Logger.warn(
        e,
        "Exception while fetching HTTP data from Wallbox: "
    )
    -1
}
