package sidev.lib.jvm.tool.util

//import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {

//    val FORMAT= "dd/MM/yyyy"

    var FORMAT_DATE= "dd/MM/yyyy"
    var FORMAT_TIME= "HH:mm:ss"
    var FORMAT_TIMESTAMP= "$FORMAT_DATE $FORMAT_TIME"
    var FORMAT_TIMESTAMP_NO_SPACE= "ddMMyyyyHHmmss"

    val month: Array<String>
        get() = arrayOf(
            "Januari",
            "Februari",
            "Maret",
            "April",
            "Mei",
            "Juni",
            "Juli",
            "Agustus",
            "September",
            "Oktober",
            "November",
            "Desember"
        )
/*
    /**
     * Sementara ini hanya untuk format dd/MM/yyyy
     * update:
     * 17 Des 2019: udah bisa format dd?MM?yyyy -> dg ? sbg @param delimiter
     */
    fun parseToAlphabet(date: String, monthLen: Int= 3, delimiter: String= "/"): String{
        val dateArr= date.split(delimiter)

        var monthName= month[dateArr[1].toInt() -1]
        if(monthLen > 0 && monthLen <= monthName.length)
            monthName= monthName.substring(0, monthLen)
        return "${dateArr[0]} $monthName ${dateArr[2]}"
    }
 */

    fun parseToNamedMonth(date: String, monthLen: Int= 3, patternIn: String= FORMAT_DATE, patternOut: String= FORMAT_DATE)
    : String{
        val sdfOut= SimpleDateFormat(patternOut, Locale.getDefault())
        val sdfIn= SimpleDateFormat(patternIn, Locale.getDefault())

        val dateObj = sdfIn.parse(date)
        return sdfOut.format(dateObj)
    }

    fun convertFormatTo(time: String, patternIn: String= FORMAT_TIMESTAMP, patternOut: String= FORMAT_DATE): String{
        val sdfOut= SimpleDateFormat(patternOut, Locale.getDefault())
        val sdfIn= SimpleDateFormat(patternIn, Locale.getDefault())

        val dateObj = sdfIn.parse(time)
        return sdfOut.format(dateObj)
    }

    fun simpleTimestamp(pattern: String= "yyyyddMMhhmmss"): String{
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return simpleDateFormat.format(Date())
    }

/*
    fun getDateString(cal: Calendar, pattern: String= "dd/MM/yyyy"): String{
        val simpleDateFormat = SimpleDateFormat(pattern)
        return simpleDateFormat.format(cal.time)
    }
 */

    /**
     * @param diff dalam millisecond
     */
    fun timestamp(cal: Calendar= Calendar.getInstance(), pattern: String= FORMAT_TIMESTAMP, diff: Long= 0): String{
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        cal.add(Calendar.MILLISECOND, diff.toInt())
        return simpleDateFormat.format(cal.time)
    }

    /**
     * Otomatis menghitung tanggal yang sesuai
     * @param month dimulai dari 1
     */
    fun timestamp(day: Int, month: Int, year: Int, pattern: String= FORMAT_TIMESTAMP): String{
        val cal= Calendar.getInstance()
        val month= if(month in 1 .. 12) month
        else if(month > 12) 12
        else 1

        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month-1)

        val dayMax= cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val day= if(day in 1 .. dayMax) day
        else if(day > dayMax) dayMax
        else 1

        cal.set(Calendar.DATE, day)

//            cal.set(year, month-1, day)
        return timestamp(
            cal,
            pattern
        )
    }

    fun toCalObj(time: String, pattern: String= FORMAT_TIMESTAMP): Calendar{
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        val cal= Calendar.getInstance()
        cal.time= simpleDateFormat.parse(time)
        return cal
    }


    /**
     * time2 - time1
     */
    fun getTimeDiff(time1: String, time2: String, format: String= FORMAT_TIMESTAMP, out: String= ""): Long{
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())

        val date1 = simpleDateFormat.parse(time1) //"08:00 AM"
        val date2 = simpleDateFormat.parse(time2) //"04:00 PM"

        val diff= date2.time - date1.time
        val res= when(out){
            "d" -> diff / (1000 *60 *60 *24) //.toInt()
            "h" -> diff / (1000 *60 *60)
            "m" -> diff / (1000 *60) //(difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) /*as Int*/ / (1000 * 60)
            "s" -> diff / (1000)
            else -> diff
        }
//        hours = if (hours < 0) -hours else hours
//        Log.e("TIME_UTIL", " :: diff= $diff out= $out res= $res")
        return res
    }
}