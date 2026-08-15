package live.lb_trip.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import live.lb_trip.data.di.qualifier.Auth
import live.lb_trip.data.dto.request.TourReceiptCreateRequestDto
import live.lb_trip.data.dto.request.TourReceiptUpdateRequestDto
import live.lb_trip.data.dto.response.ReceiptDetailResponseDto
import live.lb_trip.data.dto.response.ReceiptDownloadUrlResponseDto
import live.lb_trip.data.dto.response.ReceiptResponseDto
import live.lb_trip.data.dto.response.ReceiptScanResponseDto
import live.lb_trip.data.dto.response.ReceiptSummaryResponseDto
import live.lb_trip.data.dto.response.bodyOrThrow
import live.lb_trip.data.dto.response.checkOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptRemoteDataSource @Inject constructor(
    @Auth private val authClient: HttpClient,
) {
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

    suspend fun getReceiptDetail(savedCourseId: Long, receiptId: Long): ReceiptDetailResponseDto =
        authClient.get("/saved-courses/$savedCourseId/receipts/$receiptId").bodyOrThrow()

    suspend fun updateReceipt(
        savedCourseId: Long,
        receiptId: Long,
        request: TourReceiptUpdateRequestDto,
    ): ReceiptDetailResponseDto =
        authClient.patch("/saved-courses/$savedCourseId/receipts/$receiptId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyOrThrow()

    suspend fun deleteReceipt(savedCourseId: Long, receiptId: Long) {
        authClient.delete("/saved-courses/$savedCourseId/receipts/$receiptId").checkOrThrow()
    }

    suspend fun getReceiptDownloadUrl(savedCourseId: Long, receiptId: Long): ReceiptDownloadUrlResponseDto =
        authClient.get("/saved-courses/$savedCourseId/receipts/$receiptId/download-url").bodyOrThrow()
}
