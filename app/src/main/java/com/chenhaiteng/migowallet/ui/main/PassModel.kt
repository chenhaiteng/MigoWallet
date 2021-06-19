package com.chenhaiteng.migowallet.ui.main

import java.time.Duration
import java.time.LocalDateTime

// TODO: To make PassType more abstract
//enum class ExpireRule {
//    FixDuration,    // For hour pass, or any pass with fixed duration, i.e expire_time = start_time + duration
//    CrossTimeline   // For day pass, or any pass expire at the end of time unit, i.e expire_time = start_time + duration + time_to_next_time_unit
//}

enum class PassType {
    Day,
    Hour
}

interface Pass {
    val type: PassType
    var activeDate: LocalDateTime?
    var expireDate: LocalDateTime?
    var duration: Duration
    var price: Double
    var insertDate: LocalDateTime?

    fun isExpired() : Boolean
    fun copyTo() : Pass
}

fun Pass.activate() {
    activeDate ?: run {
        synchronized(this@activate) {
            activeDate ?: run {
                activeDate = LocalDateTime.now()
            }
        }
    }
}

fun Pass.isActivated() : Boolean {
    return activeDate != null
}

fun Pass.title(): String = when(type) {
    PassType.Day -> "${duration.toDays()} Day Pass".toUpperCase()
    PassType.Hour -> "${duration.toHours()} Hour Pass".toUpperCase()
}

data class DayPass(override var duration: Duration, override var price: Double = 0.0): Pass {
    constructor(num: Long, price: Double)  : this(Duration.ofDays(num), price)

    override val type: PassType = PassType.Day
    override var activeDate: LocalDateTime? = null
        set(value) {
            if (value != field) {
                field = value
                field?.let {
                    expireDate = it.plus(duration)
                }
            }
        }
    override var expireDate: LocalDateTime? = null
    override var insertDate: LocalDateTime? = null

    override fun isExpired() : Boolean = isExpired(LocalDateTime.now())
    override fun copyTo(): Pass = copy()
    // To make DayPass testable
    fun isExpired(atDate: LocalDateTime) : Boolean {
        return expireDate?.let {
            if (atDate.year == it.year) {
                atDate.dayOfYear > it.dayOfYear
            } else atDate.year > it.year
        } ?: true
    }
}

data class HourPass(override var duration: Duration, override var price: Double = 0.0): Pass {
    constructor(num: Long, price: Double)  : this(Duration.ofHours(num), price)

    override val type: PassType = PassType.Hour
    override var activeDate: LocalDateTime? = null
        set(value) {
            if(value != field) {
                field = value
                field?.let {
                    expireDate = it.plus(duration)
                }
            }
        }
    override var expireDate: LocalDateTime? = null
    override var insertDate: LocalDateTime? = null

    override fun isExpired() : Boolean = isExpired(LocalDateTime.now())
    override fun copyTo(): Pass = copy()

    // To make DayPass testable
    fun isExpired(atDate: LocalDateTime) : Boolean {
        return expireDate?.let {
            it <= atDate
        } ?: true
    }
}
