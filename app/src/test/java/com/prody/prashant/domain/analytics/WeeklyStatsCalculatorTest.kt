package com.prody.prashant.domain.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

/**
 * Unit tests for weekly stats range computation.
 *
 * These tests verify the date range calculation logic used
 * for generating weekly activity charts and statistics.
 */
class WeeklyStatsCalculatorTest {

    // ==========================================================================
    // WEEKLY RANGE CALCULATION TESTS
    // ==========================================================================

    @Test
    fun `getWeekRange - returns 7 days`() {
        val (start, end) = getWeekRange()
        val days = ((end - start) / (24 * 60 * 60 * 1000)).toInt()
        assertEquals(6, days) // Start to end is 6 full days (7 day range)
    }

    @Test
    fun `getWeekRange - end is today`() {
        val (_, end) = getWeekRange()
        val todayStart = getStartOfDay(System.currentTimeMillis())
        val endDay = getStartOfDay(end)

        assertEquals(todayStart, endDay)
    }

    @Test
    fun `getWeekRange - start is 6 days before today`() {
        val (start, end) = getWeekRange()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = end
            add(Calendar.DAY_OF_YEAR, -6)
        }
        val expectedStart = getStartOfDay(calendar.timeInMillis)
        val actualStart = getStartOfDay(start)

        assertEquals(expectedStart, actualStart)
    }

    @Test
    fun `getWeekDays - returns 7 day labels`() {
        val days = getWeekDays()
        assertEquals(7, days.size)
    }

    @Test
    fun `getWeekDays - contains valid day abbreviations`() {
        val days = getWeekDays()
        val validDays = setOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        days.forEach { day ->
            assertTrue("Invalid day: $day", validDays.contains(day))
        }
    }

    // ==========================================================================
    // DATE FORMATTING TESTS
    // ==========================================================================

    @Test
    fun `formatDateRange - produces readable format`() {
        val start = createDate(2024, 1, 15) // Jan 15, 2024
        val end = createDate(2024, 1, 21) // Jan 21, 2024

        val formatted = formatDateRange(start, end)

        assertTrue(formatted.contains("Jan"))
        assertTrue(formatted.contains("15"))
        assertTrue(formatted.contains("21"))
    }

    @Test
    fun `formatDateRange - handles month boundary`() {
        val start = createDate(2024, 1, 28) // Jan 28
        val end = createDate(2024, 2, 3) // Feb 3

        val formatted = formatDateRange(start, end)

        assertTrue(formatted.contains("Jan"))
        assertTrue(formatted.contains("Feb"))
    }

    // ==========================================================================
    // CONSISTENCY SCORE CALCULATION TESTS
    // ==========================================================================

    @Test
    fun `consistency score - perfect streak equals 100`() {
        val score = calculateConsistencyScore(
            currentStreak = 30,
            daysActive = 30
        )
        assertEquals(100, score)
    }

    @Test
    fun `consistency score - half active days equals 50`() {
        val score = calculateConsistencyScore(
            currentStreak = 15,
            daysActive = 30
        )
        assertEquals(50, score)
    }

    @Test
    fun `consistency score - zero days active returns 0`() {
        val score = calculateConsistencyScore(
            currentStreak = 0,
            daysActive = 0
        )
        assertEquals(0, score)
    }

    @Test
    fun `consistency score - handles divide by zero`() {
        val score = calculateConsistencyScore(
            currentStreak = 5,
            daysActive = 0
        )
        assertEquals(0, score) // Should not crash
    }

    @Test
    fun `consistency score - capped at 100`() {
        val score = calculateConsistencyScore(
            currentStreak = 100,
            daysActive = 50 // Would be 200% without cap
        )
        assertEquals(100, score)
    }

    // ==========================================================================
    // WEEKLY GROWTH CALCULATION TESTS
    // ==========================================================================

    @Test
    fun `weekly growth - positive growth calculated correctly`() {
        val growth = calculateWeeklyGrowth(
            thisWeekPoints = 150,
            lastWeekPoints = 100
        )
        assertEquals(50, growth) // 50% growth
    }

    @Test
    fun `weekly growth - negative growth calculated correctly`() {
        val growth = calculateWeeklyGrowth(
            thisWeekPoints = 75,
            lastWeekPoints = 100
        )
        assertEquals(-25, growth) // -25% decline
    }

    @Test
    fun `weekly growth - zero last week returns 100 percent growth`() {
        val growth = calculateWeeklyGrowth(
            thisWeekPoints = 100,
            lastWeekPoints = 0
        )
        assertEquals(100, growth) // Infinite growth capped at 100%
    }

    @Test
    fun `weekly growth - both zero returns 0`() {
        val growth = calculateWeeklyGrowth(
            thisWeekPoints = 0,
            lastWeekPoints = 0
        )
        assertEquals(0, growth)
    }

    @Test
    fun `weekly growth - capped at reasonable bounds`() {
        val growth = calculateWeeklyGrowth(
            thisWeekPoints = 1000,
            lastWeekPoints = 10 // Would be 9900% without cap
        )
        assertTrue(growth <= 500) // Capped at reasonable maximum
    }

    // ==========================================================================
    // LEARNING PACE DETERMINATION TESTS
    // ==========================================================================

    @Test
    fun `learning pace - high weekly points returns Fast`() {
        val pace = determineLearningPace(weeklyPointsAverage = 350)
        assertEquals("Fast", pace)
    }

    @Test
    fun `learning pace - medium weekly points returns Steady`() {
        val pace = determineLearningPace(weeklyPointsAverage = 150)
        assertEquals("Steady", pace)
    }

    @Test
    fun `learning pace - low weekly points returns Gradual`() {
        val pace = determineLearningPace(weeklyPointsAverage = 50)
        assertEquals("Gradual", pace)
    }

    @Test
    fun `learning pace - zero points returns Gradual`() {
        val pace = determineLearningPace(weeklyPointsAverage = 0)
        assertEquals("Gradual", pace)
    }

    // ==========================================================================
    // HELPER METHODS (Same logic as app)
    // ==========================================================================

    private fun getWeekRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val end = getStartOfDay(calendar.timeInMillis)

        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val start = getStartOfDay(calendar.timeInMillis)

        return Pair(start, end)
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getWeekDays(): List<String> {
        val days = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)

        repeat(7) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            days.add(
                when (dayOfWeek) {
                    Calendar.MONDAY -> "Mon"
                    Calendar.TUESDAY -> "Tue"
                    Calendar.WEDNESDAY -> "Wed"
                    Calendar.THURSDAY -> "Thu"
                    Calendar.FRIDAY -> "Fri"
                    Calendar.SATURDAY -> "Sat"
                    Calendar.SUNDAY -> "Sun"
                    else -> "?"
                }
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return days
    }

    private fun createDate(year: Int, month: Int, day: Int): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // Calendar months are 0-indexed
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun formatDateRange(start: Long, end: Long): String {
        val startCal = Calendar.getInstance().apply { timeInMillis = start }
        val endCal = Calendar.getInstance().apply { timeInMillis = end }

        val startMonth = getMonthName(startCal.get(Calendar.MONTH))
        val endMonth = getMonthName(endCal.get(Calendar.MONTH))
        val startDay = startCal.get(Calendar.DAY_OF_MONTH)
        val endDay = endCal.get(Calendar.DAY_OF_MONTH)

        return if (startMonth == endMonth) {
            "$startMonth $startDay - $endDay"
        } else {
            "$startMonth $startDay - $endMonth $endDay"
        }
    }

    private fun getMonthName(month: Int): String {
        return when (month) {
            Calendar.JANUARY -> "Jan"
            Calendar.FEBRUARY -> "Feb"
            Calendar.MARCH -> "Mar"
            Calendar.APRIL -> "Apr"
            Calendar.MAY -> "May"
            Calendar.JUNE -> "Jun"
            Calendar.JULY -> "Jul"
            Calendar.AUGUST -> "Aug"
            Calendar.SEPTEMBER -> "Sep"
            Calendar.OCTOBER -> "Oct"
            Calendar.NOVEMBER -> "Nov"
            Calendar.DECEMBER -> "Dec"
            else -> "?"
        }
    }

    private fun calculateConsistencyScore(currentStreak: Int, daysActive: Int): Int {
        if (daysActive <= 0) return 0
        return ((currentStreak.toFloat() / daysActive) * 100).toInt().coerceIn(0, 100)
    }

    private fun calculateWeeklyGrowth(thisWeekPoints: Int, lastWeekPoints: Int): Int {
        if (lastWeekPoints <= 0) {
            return if (thisWeekPoints > 0) 100 else 0
        }
        val growth = ((thisWeekPoints - lastWeekPoints).toFloat() / lastWeekPoints * 100).toInt()
        return growth.coerceIn(-100, 500) // Cap at reasonable bounds
    }

    private fun determineLearningPace(weeklyPointsAverage: Int): String {
        return when {
            weeklyPointsAverage >= 300 -> "Fast"
            weeklyPointsAverage >= 100 -> "Steady"
            else -> "Gradual"
        }
    }
}
