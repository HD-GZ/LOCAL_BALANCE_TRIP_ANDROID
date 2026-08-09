package live.lb_trip.domain.exception

fun sharedInvalidFieldMessage(field: String): String? =
    when (field) {
        "password" -> "비밀번호는 영문·숫자 포함 8자 이상이어야 해요."
        "passwordConfirm" -> "비밀번호가 일치하지 않아요."
        "name" -> "이름을 확인해 주세요."
        "birthDate" -> "생년월일을 확인해 주세요."
        else -> null
    }
