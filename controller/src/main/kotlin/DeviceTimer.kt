import org.tinylog.kotlin.Logger
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DeviceTimer(
    private val turnOnHour: Int,
    private val turnOnMinute: Int,
    private val turnOffHour: Int,
    private val turnOffMinute: Int,
    private val turnOnJob: () -> Unit,
    private val turnOffJob: () -> Unit,
    private val clock: Clock = Clock.systemDefaultZone(),
    private val name: String = "",
) {

    private val turnOnJobWrapped = {
        Logger.info("Turn ON $name. Turns off at ${turnOffExecutor.zonedNextTargetTime}.")
        try {
            turnOnJob()
        } catch (throwable: Throwable) {
            Logger.error("$name: Error in turnOnJob.", throwable)
        }
    }

    private val turnOffJobWrapped = {
        Logger.info("Turn OFF $name. Turns on at ${turnOnExecutor.zonedNextTargetTime}.")
        try {
            turnOffJob()
        } catch (throwable: Throwable) {
            Logger.error("$name: Error in turnOffJob.", throwable)
        }
    }

    private val turnOnExecutor: TaskExecutor =
        TaskExecutor(turnOnJobWrapped, turnOnHour, turnOnMinute, clock = clock)
    private val turnOffExecutor: TaskExecutor =
        TaskExecutor(turnOffJobWrapped, turnOffHour, turnOffMinute, clock = clock)

    fun start() {
        val currentZone: ZoneId = ZoneId.systemDefault()
        val zonedNow: ZonedDateTime =
            ZonedDateTime.of(LocalDateTime.now(clock), currentZone)
        val turnOnTime: ZonedDateTime = zonedNow.withHour(turnOnHour)
            .withMinute(turnOnMinute)
        val turnOffTime: ZonedDateTime = zonedNow.withHour(turnOffHour)
            .withMinute(turnOffMinute)

        // equipment runs overnight
        if (turnOnTime.isAfter(turnOffTime)) {
            if (zonedNow.isAfter(turnOnTime) || zonedNow.isBefore(turnOffTime)) {
                turnOnJobWrapped()
            } else {
                turnOffJobWrapped()
            }
        } else {
            if (zonedNow.isAfter(turnOnTime) && zonedNow.isBefore(turnOffTime)) {
                turnOnJobWrapped()
            } else {
                turnOffJobWrapped()
            }
        }

        turnOnExecutor.scheduleAtTargetTime()
        turnOffExecutor.scheduleAtTargetTime()
    }

    val nextTurnOffTime: ZonedDateTime
        get() = turnOffExecutor.zonedNextTargetTime

    val nextTurnOnTime: ZonedDateTime
        get() = turnOnExecutor.zonedNextTargetTime
}
