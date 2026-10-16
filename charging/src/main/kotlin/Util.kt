import java.util.Date
import kotlin.time.Instant
import kotlin.time.toJavaInstant

fun Instant.toJavaDate() = Date.from(this.toJavaInstant())