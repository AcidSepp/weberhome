import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.Test

class DeviceTimerTest {
    @Test
    fun testRunningDuringDayTurnedOn() {
        val middayClock = Clock.fixed(
            Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(12)),
            ZoneId.systemDefault()
        )

        val turnOnJobRan = AtomicBoolean(false)
        val turnOffJobRan = AtomicBoolean(false)

        val turnOnJob = {
            turnOnJobRan.set(true)
        }

        val turnOffJob = {
            turnOffJobRan.set(true)
        }

        DeviceTimer(
            10, 0, 18, 0, turnOnJob, turnOffJob, middayClock
        ).start()

        assertTrue(turnOnJobRan.get())
        assertFalse(turnOffJobRan.get())
    }

    @Test
    fun testRunningDuringDayTurnedOff() {
        val midnightClock = Clock.fixed(
            Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(0)),
            ZoneId.systemDefault()
        )

        val turnOnJobRan = AtomicBoolean(false)
        val turnOffJobRan = AtomicBoolean(false)

        val turnOnJob = {
            turnOnJobRan.set(true)
        }

        val turnOffJob = {
            turnOffJobRan.set(true)
        }

        DeviceTimer(
            10, 0, 18, 0, turnOnJob, turnOffJob, midnightClock
        ).start()

        assertFalse(turnOnJobRan.get())
        assertTrue(turnOffJobRan.get())
    }

    @Test
    fun testRunningDuringNightTurnedOn() {
        val middayClock = Clock.fixed(
            Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(12)),
            ZoneId.systemDefault()
        )

        val turnOnJobRan = AtomicBoolean(false)
        val turnOffJobRan = AtomicBoolean(false)

        val turnOnJob = {
            turnOnJobRan.set(true)
        }

        val turnOffJob = {
            turnOffJobRan.set(true)
        }

        DeviceTimer(
            18, 0, 10, 0, turnOnJob, turnOffJob, middayClock
        ).start()

        assertFalse(turnOnJobRan.get())
        assertTrue(turnOffJobRan.get())
    }

    @Test
    fun testRunningDuringNightTurnedOff() {
        val midnightClock = Clock.fixed(
            Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(0)),
            ZoneId.systemDefault()
        )

        val turnOnJobRan = AtomicBoolean(false)
        val turnOffJobRan = AtomicBoolean(false)

        val turnOnJob = {
            turnOnJobRan.set(true)
        }

        val turnOffJob = {
            turnOffJobRan.set(true)
        }

        DeviceTimer(
            18, 0, 10, 0, turnOnJob, turnOffJob, midnightClock
        ).start()

        assertTrue(turnOnJobRan.get())
        assertFalse(turnOffJobRan.get())
    }
}