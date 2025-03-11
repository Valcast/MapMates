package com.example.socialmeetingapp.domain.model

import kotlinx.datetime.LocalDateTime

sealed class DateRange {
    data object Today : DateRange()
    data object Tomorrow : DateRange()
    data object ThisWeek : DateRange()
    data class Custom(val startTime: LocalDateTime, val endTime: LocalDateTime) : DateRange()
}