import java.util.Date
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.time.Instant
import kotlin.time.toJavaInstant

fun Instant.toJavaDate(): Date = Date.from(this.toJavaInstant())

fun Int.almostEquals(
    other: Int,
    delta: Int,
) = abs(this - other) < delta

fun powerToMilliAmps(powerWatts: Int) =
    (powerWatts / (400f * sqrt(3f))) * 1000f