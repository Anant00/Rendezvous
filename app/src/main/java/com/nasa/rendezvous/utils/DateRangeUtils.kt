package com.nasa.rendezvous.utils

import java.text.SimpleDateFormat
import java.util.*

object DateRangeUtils {

    fun getDaysBackDate(daysAgo: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -daysAgo)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return formatter.format(cal.time)
    }

    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date = Date()
        return dateFormat.format(date)
    }

    // get new dates from here.
    private fun getNewDates(startDate: String, daysAgo: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date = dateFormat.parse(startDate)
        val cal: Calendar = Calendar.getInstance()
        cal.time = date!!
        cal.add(Calendar.DAY_OF_MONTH, -daysAgo)
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return formatter.format(cal.time)
    }

    fun updateDate(oldStartDate: String): DateObj {
        /* new start date = old end date - 1 previous data; for ex. let 2019-09-25  be the end data
            and 2019-09-10 is the start date. Therefore, for the first time, the data will be fetched from start to end date.
            Now on onLoadMore() we need to pass new dates. We need data from b/w 2019-09-09 ( new end data = old end date -1)
            and 2019-08-25 (new start date = old end date - 16 )
         */
        val newEndDate = getNewDates(oldStartDate, 1)
        val newStartDate = getNewDates(oldStartDate, 16)
        return DateObj(newStartDate, newEndDate)
    }

    data class DateObj(var startDate: String, var endDate: String)

}