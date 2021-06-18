package com.chenhaiteng.migowallet.ui.main.placeholder
import com.chenhaiteng.migowallet.ui.main.*
import java.time.Duration

// Mock Model for PassShop
class MockShop {
    private var items = listOf<Pass>()
    private fun days(num: Long) = Duration.ofDays(num)
    private fun hours(num: Long)  = Duration.ofHours(num)
    fun fetchAvailablePasses() {
        items = listOf(
            DayPass(1, 2.0),
            DayPass(3, 5.0),
            DayPass(7, 10.0),
            HourPass(1, 0.5),
            HourPass(8, 1.0)
        )
    }

    // return copy to avoid the data be modified unexpected
    fun allPass() = items.map {
        when(it) {
            is DayPass -> {
                it.copy()
            }
            is HourPass -> {
                it.copy()
            }
            else -> it
        }
    }

    fun availableDayPass() = items.filter {
        it.type == PassType.Day
    }.map {
        (it as DayPass).copy()
    }

    fun availableHourPass() = items.filter {
        it.type == PassType.Hour
    }.map {
        (it as HourPass).copy()
    }
}