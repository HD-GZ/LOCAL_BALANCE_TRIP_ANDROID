package live.lb_trip.data.exception

class NetworkException(
    val code: String,
    override val message: String,
) : Exception(message)
