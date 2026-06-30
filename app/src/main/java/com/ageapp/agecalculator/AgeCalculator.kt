package com.ageapp.agecalculator

import java.time.LocalDate
import java.time.Period
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Immutable result describing the difference between two dates plus a set of
 * commonly-wanted derived details.
 */
data class AgeResult(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalMonths: Long,
    val totalWeeks: Long,
    val totalDays: Long,
    val totalHours: Long,
    val totalMinutes: Long,
    val totalSeconds: Long,
    val bornDayOfWeek: String,
    val zodiac: String,
    val nextBirthday: LocalDate,
    val nextBirthdayDayOfWeek: String,
    val daysUntilNextBirthday: Long,
    val turningAge: Int,
    val isBirthdayToday: Boolean
)

object AgeCalculator {

    /**
     * Calculate the age (and extra details) between [birthDate] and [toDate].
     *
     * @throws IllegalArgumentException if [birthDate] is after [toDate].
     */
    fun calculate(birthDate: LocalDate, toDate: LocalDate): AgeResult {
        require(!birthDate.isAfter(toDate)) {
            "Date of birth cannot be after the target date."
        }

        val period = Period.between(birthDate, toDate)

        val totalDays = ChronoUnit.DAYS.between(birthDate, toDate)
        val totalWeeks = totalDays / 7
        val totalMonths = ChronoUnit.MONTHS.between(birthDate, toDate)
        val totalHours = totalDays * 24
        val totalMinutes = totalHours * 60
        val totalSeconds = totalMinutes * 60

        val locale = Locale.getDefault()
        val bornDow = birthDate.dayOfWeek.getDisplayName(TextStyle.FULL, locale)

        // Next birthday: shift the birth month/day into the target year.
        // withYear() automatically clamps Feb 29 to Feb 28 in non-leap years.
        var nextBirthday = birthDate.withYear(toDate.year)
        if (nextBirthday.isBefore(toDate)) {
            nextBirthday = birthDate.withYear(toDate.year + 1)
        }
        val isBirthdayToday = nextBirthday.isEqual(toDate)
        val daysUntil = ChronoUnit.DAYS.between(toDate, nextBirthday)
        val nextBirthdayDow = nextBirthday.dayOfWeek.getDisplayName(TextStyle.FULL, locale)
        val turningAge = nextBirthday.year - birthDate.year

        return AgeResult(
            years = period.years,
            months = period.months,
            days = period.days,
            totalMonths = totalMonths,
            totalWeeks = totalWeeks,
            totalDays = totalDays,
            totalHours = totalHours,
            totalMinutes = totalMinutes,
            totalSeconds = totalSeconds,
            bornDayOfWeek = bornDow,
            zodiac = zodiacSign(birthDate),
            nextBirthday = nextBirthday,
            nextBirthdayDayOfWeek = nextBirthdayDow,
            daysUntilNextBirthday = daysUntil,
            turningAge = turningAge,
            isBirthdayToday = isBirthdayToday
        )
    }

    /** Western (sun) zodiac sign for the given date. */
    fun zodiacSign(date: LocalDate): String {
        val d = date.dayOfMonth
        return when (date.monthValue) {
            1 -> if (d < 20) "Capricorn" else "Aquarius"
            2 -> if (d < 19) "Aquarius" else "Pisces"
            3 -> if (d < 21) "Pisces" else "Aries"
            4 -> if (d < 20) "Aries" else "Taurus"
            5 -> if (d < 21) "Taurus" else "Gemini"
            6 -> if (d < 21) "Gemini" else "Cancer"
            7 -> if (d < 23) "Cancer" else "Leo"
            8 -> if (d < 23) "Leo" else "Virgo"
            9 -> if (d < 23) "Virgo" else "Libra"
            10 -> if (d < 23) "Libra" else "Scorpio"
            11 -> if (d < 22) "Scorpio" else "Sagittarius"
            12 -> if (d < 22) "Sagittarius" else "Capricorn"
            else -> ""
        }
    }
}
