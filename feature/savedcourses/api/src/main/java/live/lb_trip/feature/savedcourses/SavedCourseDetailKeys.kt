package live.lb_trip.feature.savedcourses

const val RECEIPT_REGISTERED_RESULT_KEY = "receiptRegistered"
const val TOUR_ENDED_RESULT_KEY = "tourEnded"
const val TOUR_ENDED_SHOW_REPORT_RESULT_KEY = "tourEndedShowReport"

/** 여행이 시작돼 코스 상태가 바뀌었음을 상세 화면에 알린다. */
const val TOUR_STARTED_RESULT_KEY = "tourStarted"

/** 저장한 코스 '목록' 창에만 전달되는 갱신 신호(상세 창 신호와 소비자가 달라 키를 분리한다). */
const val SAVED_COURSES_REFRESH_RESULT_KEY = "savedCoursesRefresh"
