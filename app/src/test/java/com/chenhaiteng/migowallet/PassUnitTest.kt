package com.chenhaiteng.migowallet

import android.util.Log
import com.chenhaiteng.migowallet.ui.main.DayPass
import com.chenhaiteng.migowallet.ui.main.HourPass
import com.chenhaiteng.migowallet.ui.main.placeholder.MockShop
import org.junit.Test

import org.junit.Assert.*
import java.time.*
import java.time.temporal.ChronoUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PassUnitTest {
    val duration_1_hour = Duration.ofHours(1)
    val duration_1_day = Duration.ofDays(1)

    @Test
    fun hourPassExpire() {
        val now = LocalDateTime.now()
        val activeTimeNotExpired = now.minusMinutes(59)
        val activeTimeExpired = now.minusMinutes(61)
        val justExpired = now.minusMinutes(60)
        val hourPass = HourPass(duration_1_hour)

        // Test no activated
        assertTrue("Should expired due to not activated! ${hourPass.expireDate ?: run {"no expired date"}}", hourPass.isExpired(now) )

        // For each active date, it has two test:
        // 1. Test basic expired logic with specified datetime
        // 2. Test real case that has no specified datetime.
        hourPass.activeDate = activeTimeNotExpired
        assertFalse("Should not expired at ${hourPass.expireDate}", hourPass.isExpired(now))
        assertFalse("Should not expired at ${hourPass.expireDate}", hourPass.isExpired())

        hourPass.activeDate = activeTimeExpired
        assertTrue("Should expired at ${hourPass.expireDate}", hourPass.isExpired(now))
        assertTrue("Should expired at ${hourPass.expireDate}", hourPass.isExpired())

        hourPass.activeDate = justExpired
        assertTrue("Just expired at ${hourPass.expireDate}", hourPass.isExpired(now))
        assertTrue("Just expired at ${hourPass.expireDate}", hourPass.isExpired())
    }

    @Test
    fun dayPassExpire() {
        val now = LocalDateTime.now()
        val today = LocalDate.now()
        val endOfToday = LocalDateTime.of(today, LocalTime.MAX)
        val startOfTomorrow = LocalDateTime.of(today.plusDays(1), LocalTime.MIDNIGHT)
        val activeTimeNotExpired = now.minusDays(1) // Should expired at today 24:00
        val activeTimeExpired = today.minusDays(1).atStartOfDay().minusNanos(1)
        val justExpired = today.minusDays(1).atStartOfDay()
        val dayPass = DayPass(duration_1_day)

        // Test no activate
        assertTrue("Should expired due to not activated! ${dayPass.expireDate ?: run {"no expired date"}}", dayPass.isExpired(now) )

        // For each active date, it has two test:
        // 1. Test basic expired logic with specified datetime
        // 2. Test real case that has no specified datetime.
        dayPass.activeDate = activeTimeNotExpired
        assertFalse("Should not expired at ${dayPass.expireDate}", dayPass.isExpired(now))
        assertFalse("shoud not expired at ${dayPass.expireDate}", dayPass.isExpired(endOfToday)) // Test end of day
        assertFalse("Should not expired at ${dayPass.expireDate}", dayPass.isExpired())

        dayPass.activeDate = activeTimeExpired
        assertTrue("Should expired at ${dayPass.expireDate}", dayPass.isExpired(now))
        assertTrue("Should expired at ${dayPass.expireDate}", dayPass.isExpired(startOfTomorrow))
        assertTrue("Should expired at ${dayPass.expireDate}", dayPass.isExpired())


        // If activate at 1/1 0:00, will expire at 1/2 0:00, but according to day pass rule, it will actual expired at 1/3 0:00.
        dayPass.activeDate = justExpired
        assertFalse("Just expired at ${dayPass.expireDate}", dayPass.isExpired(now))
    }
}