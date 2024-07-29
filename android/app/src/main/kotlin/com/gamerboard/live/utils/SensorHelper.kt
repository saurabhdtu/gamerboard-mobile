package com.gamerboard.live.utils

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


class SensorHelper(val context: Context) {
    private val job = SupervisorJob()
    private val sensorData: HashMap<String, Float> = hashMapOf()

    companion object {
        private val TAG = SensorHelper::class.java.simpleName
        var gpuTemp: Float = 0.0f
            get() {
                return field
            }
            private set(value) {
                field = value
            }
        var cpuTemp: Float = 0.0f
            get() {
                return field
            }
            private set(value) {
                field = value
            }
    }

    fun startListening() {
        CoroutineScope(Dispatchers.IO + job).launch {
            while (!job.isCancelled) {
                thermal()
                delay(5000)
                gpuTemp = averageGpuTemp()
                cpuTemp = averageCpuTemp()
            }
        }
    }

    fun averageCpuTemp(): Float {
        val values = sensorData.filter { it.key.contains("cpu") }.values
        if (values.isNotEmpty())
            return values.sum() / values.size
        return 0f
    }

    fun averageGpuTemp(): Float {
        val values = sensorData.filter { it.key.contains("gpu") }.values
        if (values.isNotEmpty())
            return values.sum() / values.size
        return 0f
    }

    private fun thermal() {
        var type: String?
        for (i in 0..28) {
            val temp = thermalTemp(i) ?: 0f
            if (temp > 0f) {
                type = thermalType(i)
                if (type != null) {
                    sensorData[type] = temp
                }
            }
        }
    }

    //https://stackoverflow.com/questions/51948718/how-to-get-android-cpu-temperature-programmatically
    private fun thermalTemp(i: Int): Float {
        val process: Process
        val reader: BufferedReader
        val line: String?
        var temp = 0f
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone$i/temp")
            process.waitFor()
            reader = BufferedReader(InputStreamReader(process.inputStream))
            line = reader.readLine()
            if (line != null) {
                temp = line.toFloat()
            }
            reader.close()
            process.destroy()
            if (temp.toInt() != 0) {
                if (temp.toInt() > 10000) {
                    temp /= 1000
                } else if (temp.toInt() > 1000) {
                    temp /= 100
                } else if (temp.toInt() > 100) {
                    temp /= 10
                }

            } else temp = 0f
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return temp
    }

    private fun thermalType(i: Int): String? {
        val process: Process
        val reader: BufferedReader
        val line: String?
        var type: String? = null
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone$i/type")
            process.waitFor()
            reader = BufferedReader(InputStreamReader(process.inputStream))
            line = reader.readLine()
            if (line != null) {
                type = line
            }
            reader.close()
            process.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return type
    }


    fun stopListening() {
        job.cancel()
    }
}