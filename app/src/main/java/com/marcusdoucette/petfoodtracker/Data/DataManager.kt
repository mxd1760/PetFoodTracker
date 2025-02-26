package com.marcusdoucette.petfoodtracker.Data

import android.content.Context
import android.util.Log
import com.marcusdoucette.petfoodtracker.MainActivity
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
import java.nio.ByteBuffer


const val AppFileName = "data.dat"
const val BitSize = 16384 //2.pow(14)

data class Week(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val booleans:List<Boolean> = listOf(
        false,false,
        false,false,
        false,false,
        false,false,
        false,false,
        false,false,
        false,false
    )
)

object DataManager{

    val myEpoch:LocalDate = LocalDate(2025,1,5) // const
    val data:MutableMap<Int,Int> = mutableMapOf()

    fun LoadData(context: Context){
        try{
            val file = context.openFileInput(AppFileName)
            val bytes = file.readBytes()
            //Log.i(MainActivity.logTag,"Bytes Size on load: ${bytes.size}")
            file.close()

            val intArray = toIntArray(bytes)
            intArray.forEach {
                    val (week,bools) = parseCustomInt(it)
                    data.put(week,bools)
            }
        }catch (e:Exception){
            Log.d(MainActivity.logTag,"No File Found")
        }
    }
    fun toIntArray(bytes:ByteArray):IntArray{
        val out = IntArray(bytes.size/4)
        for (i in 0 until bytes.size/4){
            out[i] = (bytes[i * 4].toInt() and 0xFF shl 24) or
                    (bytes[i * 4 + 1].toInt() and 0xFF shl 16) or
                    (bytes[i * 4 + 2].toInt() and 0xFF shl 8) or
                    (bytes[i * 4 + 3].toInt() and 0xFF)
        }
        return out;
    }
    fun SaveData(context:Context){
        try{
            val buffer = ByteBuffer.allocate(data.size*4)
            for(week in data.keys){
                val bools = data[week]?:0
                val entry = convertToCustomInt(week,bools)
                buffer.putInt(entry)
            }
            val byteArray = buffer.array()

            //Log.i(MainActivity.logTag,"Bytes Size on save: ${byteArray.size}")
            val file = context.openFileOutput(AppFileName,Context.MODE_PRIVATE)
            file.write(byteArray)
            file.close()
        }catch (e:Exception){
            Log.d(MainActivity.logTag,"Failed to save")
        }
    }
    fun GetWeek(date:LocalDate,offset:Int = 0):Week{
        var weekInt:Int = weekIntFromDate(date)
        weekInt += offset
        val dataInt = data[weekInt]?:0
        return convertToWeek(weekInt,dataInt)
    }
    fun PostWeek(item:Week){
        var week = weekIntFromDate(item.endDate)
        var bools = intFromBools(item.booleans.toList())
        data[week] = bools
    }
    fun intFromBools(bools:List<Boolean>):Int{
        var out = 0
        for(i in bools.size-1 downTo 0){
            out *= 2
            out += if (bools[i]) 1 else 0
        }
        return out;
    }
    fun weekIntFromDate(date:LocalDate):Int{
        // how many weeks from 2025-jan-5 (week 0)
        val dayDelta = myEpoch.daysUntil(date)
        val weeksSince = dayDelta/7
        return weeksSince
    }
    fun parseCustomInt(i:Int):Pair<Int,Int>{
        return Pair(i/BitSize, i%BitSize)
    }
    fun convertToWeek(week:Int,bools:Int):Week{
        val startDate = myEpoch.plus(week, DateTimeUnit.WEEK)
        val endDate = startDate.plus(6,DateTimeUnit.DAY)
        var b = bools
        val _data:MutableList<Boolean> = mutableListOf()
        for (i in 0 until 14){
            _data.add(b%2 == 1)
            b /= 2
        }
        return Week(startDate,endDate,_data)
    }
    fun convertToCustomInt(week:Int,bools:Int):Int{
        return week*BitSize + (bools%BitSize)
    }
    fun getCurrentDate():LocalDate{
        return java.time.LocalDate.now().toKotlinLocalDate()
    }
}