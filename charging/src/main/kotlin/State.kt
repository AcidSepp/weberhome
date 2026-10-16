import kotlinx.serialization.Serializable

@Serializable
class State(val solarOverProductionCharging: Boolean, val sessions: List<SessionWithId>)
