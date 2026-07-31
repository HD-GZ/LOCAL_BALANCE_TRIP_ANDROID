package live.lb_trip.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton
import live.lb_trip.data.di.qualifier.Auth
import live.lb_trip.data.dto.request.TourReceiptCreateRequestDto
import live.lb_trip.data.dto.response.ReceiptResponseDto
import live.lb_trip.data.dto.response.ReceiptScanResponseDto
import live.lb_trip.data.dto.response.ReceiptSummaryResponseDto
import live.lb_trip.data.dto.response.SavedCourseDetailResponseDto
import live.lb_trip.data.dto.response.SavedCourseListResponseDto
import live.lb_trip.data.dto.response.bodyOrThrow

@Singleton
class SavedCourseRemoteDataSource
    @Inject
    constructor(
        @Auth private val authClient: HttpClient,
    ) {
        suspend fun getSavedCourses(): SavedCourseListResponseDto =
            authClient.get("/saved-courses") { parameter("limit", 50) }.bodyOrThrow()

        suspend fun getSavedCourseDetail(savedCourseId: Long): SavedCourseDetailResponseDto =
            authClient.get("/saved-courses/$savedCourseId").bodyOrThrow()

        suspend fun getReceipts(savedCourseId: Long): ReceiptSummaryResponseDto =
            authClient.get("/saved-courses/$savedCourseId/receipts").bodyOrThrow()

        suspend fun scanReceipt(savedCourseId: Long, imageBytes: ByteArray, fileName: String): ReceiptScanResponseDto =
            authClient.submitFormWithBinaryData(
                url = "/saved-courses/$savedCourseId/receipts/scan",
                formData = formData {
                    append(
                        key = "image",
                        value = imageBytes,
                        headers =
                            Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                            },
                    )
                },
            ).bodyOrThrow()

        suspend fun registerReceipt(savedCourseId: Long, request: TourReceiptCreateRequestDto): ReceiptResponseDto =
            authClient.post("/saved-courses/$savedCourseId/receipts") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.bodyOrThrow()
    }
