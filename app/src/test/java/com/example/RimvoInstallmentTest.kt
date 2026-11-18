package com.example

import com.example.data.model.InstallmentCalculator
import com.example.data.model.InstallmentTenure
import com.example.data.model.formatBdt
import org.junit.Assert.assertEquals
import org.junit.Test

class RimvoInstallmentTest {

    @Test
    fun threeMonthPlanHasZeroMarkupAndExactTotal() {
        val breakdown = InstallmentCalculator.calculate(cashPriceBdt = 10000, tenure = InstallmentTenure.THREE_MONTHS)
        assertEquals(0, breakdown.markupAmountBdt)
        assertEquals(10000, breakdown.totalPayableBdt)
        assertEquals(2500, breakdown.downPaymentTodayBdt)
        assertEquals(7500, breakdown.financedPrincipalBdt)
        assertEquals(3, breakdown.schedule.size)
        assertEquals(7500, breakdown.schedule.sumOf { it.amountBdt })
    }

    @Test
    fun sixAndTwelveMonthSchedulesSumExactlyToFinancedPrincipal() {
        listOf(InstallmentTenure.SIX_MONTHS, InstallmentTenure.TWELVE_MONTHS).forEach { tenure ->
            val breakdown = InstallmentCalculator.calculate(cashPriceBdt = 32900, tenure = tenure)
            assertEquals(tenure.months, breakdown.schedule.size)
            assertEquals(
                breakdown.financedPrincipalBdt,
                breakdown.schedule.sumOf { it.amountBdt }
            )
            assertEquals(
                breakdown.totalPayableBdt,
                breakdown.downPaymentTodayBdt + breakdown.financedPrincipalBdt
            )
        }
    }

    @Test
    fun formatsBangladeshiTakaCleanly() {
        assertEquals("৳15,000", formatBdt(15000))
        assertEquals("৳32,900", formatBdt(32900))
    }
}
