import java.util.Date
import kotlin.math.abs
import kotlin.time.Instant
import kotlin.time.toJavaInstant

fun Instant.toJavaDate(): Date = Date.from(this.toJavaInstant())

fun Int.almostEquals(
    other: Int,
    delta: Int,
) = abs(this - other) < delta