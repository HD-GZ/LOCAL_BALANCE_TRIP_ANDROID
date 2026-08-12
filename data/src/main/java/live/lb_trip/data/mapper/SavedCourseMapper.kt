package live.lb_trip.data.mapper

import live.lb_trip.data.dto.response.BenefitResponseDto
import live.lb_trip.data.dto.response.ReceiptDetailResponseDto
import live.lb_trip.data.dto.response.ReceiptResponseDto
import live.lb_trip.data.dto.response.ReceiptScanResponseDto
import live.lb_trip.data.dto.response.ReceiptSummaryResponseDto
import live.lb_trip.data.dto.response.SavedCourseDetailResponseDto
import live.lb_trip.data.dto.response.SavedCourseListResponseDto
import live.lb_trip.data.dto.response.SavedCourseReportResponseDto
import live.lb_trip.data.dto.response.SavedCourseResponseDto
import live.lb_trip.domain.model.CourseBenefit
import live.lb_trip.domain.model.Receipt
import live.lb_trip.domain.model.ReceiptDetail
import live.lb_trip.domain.model.ReceiptScan
import live.lb_trip.domain.model.ReceiptSummary
import live.lb_trip.domain.model.SavedCourse
import live.lb_trip.domain.model.SavedCourseDetail
import live.lb_trip.domain.model.SavedCourseList
import live.lb_trip.domain.model.SavedCourseReport
import live.lb_trip.domain.model.TravelStatus

fun SavedCourseListResponseDto.toDomain(): SavedCourseList =
    SavedCourseList(totalCount = totalCount, courses = courses.map { it.toDomain() })

fun SavedCourseResponseDto.toDomain(): SavedCourse =
    SavedCourse(
        savedCourseId = savedCourseId,
        courseName = courseName,
        imageUrl = imageUrl,
        status = status.toTravelStatus(),
    )

fun SavedCourseDetailResponseDto.toDomain(): SavedCourseDetail =
    SavedCourseDetail(
        savedCourseId = savedCourseId,
        regionName = regionName,
        title = title,
        status = status.toTravelStatus(),
        places = places.map { it.toDomain() },
        benefits = benefits.map { it.toDomain() },
    )

fun BenefitResponseDto.toDomain(): CourseBenefit =
    CourseBenefit(title = title, description = description, url = url)

fun ReceiptSummaryResponseDto.toDomain(): ReceiptSummary =
    ReceiptSummary(totalAmount = totalAmount, receipts = receipts.map { it.toDomain() })

fun ReceiptResponseDto.toDomain(): Receipt =
    Receipt(receiptId = receiptId, merchantName = merchantName, amount = amount, paidDate = paidDate)

fun ReceiptDetailResponseDto.toDomain(): ReceiptDetail =
    ReceiptDetail(
        receiptId = receiptId,
        merchantName = merchantName,
        amount = amount,
        paidDate = paidDate,
        imageUrl = imageUrl,
    )

fun ReceiptScanResponseDto.toDomain(): ReceiptScan =
    ReceiptScan(
        imageId = imageId,
        imageUrl = imageUrl,
        merchantName = merchantName,
        amount = amount,
        paidDate = paidDate,
    )

fun SavedCourseReportResponseDto.toDomain(): SavedCourseReport =
    SavedCourseReport(
        courseName = courseName,
        imageUrl = imageUrl,
        visitedPlaceCount = visitedPlaceCount,
        durationMinutes = durationMinutes,
        totalSpentAmount = totalSpentAmount,
        tourEndedAt = tourEndedAt,
        distanceWalkedMeters = walkedDistanceMeters?.toFloat(),
        stepCount = null,
    )

private fun String.toTravelStatus(): TravelStatus = TravelStatus.valueOf(this)
