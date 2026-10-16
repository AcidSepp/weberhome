import kotlinx.serialization.Serializable
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
data class SessionWithId(val uuid: Uuid, val start: Instant, val stop: Instant, val active: Boolean)