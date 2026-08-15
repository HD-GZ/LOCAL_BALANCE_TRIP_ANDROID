package live.lb_trip.core.util

import android.content.Context
import live.lb_trip.core.R

fun sharedInvalidFieldMessage(context: Context, field: String): String? =
    when (field) {
        "password" -> context.getString(R.string.core_field_validation_password)
        "passwordConfirm" -> context.getString(R.string.core_field_validation_password_confirm)
        "name" -> context.getString(R.string.core_field_validation_name)
        "birthDate" -> context.getString(R.string.core_field_validation_birth_date)
        else -> null
    }
