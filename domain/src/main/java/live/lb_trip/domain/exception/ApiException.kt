package live.lb_trip.domain.exception

class ApiException(
    val statusCode: Int,
    val code: String = "",
    override val message: String,
) : LbTripException()
