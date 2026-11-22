import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class TaskExecutor(
    private val task: () -> Unit,
    val targetHour: Int,
    val targetMin: Int,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    val executorService: ScheduledExecutorService =
        Executors.newScheduledThreadPool(1)
    var zonedNextTargetTime: ZonedDateTime = computeNextTargetTime()

    fun scheduleAtTargetTime() {
        val taskWrapper = {
            scheduleAtTargetTime()
            try {
                task()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }
        }
        zonedNextTargetTime = computeNextTargetTime()
        executorService.schedule(
            taskWrapper, computeNextDelay(), TimeUnit.SECONDS
        )
    }

    private fun computeNextTargetTime(): ZonedDateTime {
        val localNow = LocalDateTime.now(clock)
        val currentZone: ZoneId = ZoneId.systemDefault()
        val zonedNow: ZonedDateTime = ZonedDateTime.of(localNow, currentZone)
        val newZonedNextTargetTime = zonedNow.withHour(targetHour)
            .withMinute(targetMin)
            .withSecond(0)
            .withNano(0)
        if (zonedNow >= newZonedNextTargetTime) {
            return newZonedNextTargetTime!!.plusDays(1)
        }
        return newZonedNextTargetTime
    }

    private fun computeNextDelay(): Long {
        val localNow = LocalDateTime.now(clock)
        val currentZone: ZoneId = ZoneId.systemDefault()
        val zonedNow: ZonedDateTime = ZonedDateTime.of(localNow, currentZone)
        val duration = Duration.between(zonedNow, zonedNextTargetTime)
        return duration.seconds
    }

    fun stop() {
        executorService.shutdown()
    }

}
