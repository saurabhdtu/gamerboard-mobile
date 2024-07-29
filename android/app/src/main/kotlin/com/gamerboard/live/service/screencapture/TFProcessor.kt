package com.gamerboard.live.service.screencapture

import android.util.Log
import android.util.Pair
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.models.Resolution
import com.gamerboard.live.models.TFResult
import com.gamerboard.logger.log
import com.gamerboard.logger.model.GameLogMessage
import com.gamerboard.logging.LoggingAgent
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.util.TreeMap
import java.util.function.Consumer

/**
 * Created by saurabh.lahoti on 17/08/21
 */
object TFProcessor {
    var running = false

    fun interpret(
        interpreter: Interpreter?,
        input: ByteBuffer,
        loggingAgent: LoggingAgent?,
        resolution: Resolution?
    ): List<TFResult> {
        val outputMap = TreeMap<Int, Any>()

        val boxes = Array(1) { Array(40) { FloatArray(4) } }
        val labels = Array(1) { FloatArray(40) }
        val scores = Array(1) { FloatArray(40) }
        val length = FloatArray(1)
        outputMap[0] = boxes
        outputMap[1] = labels
        outputMap[2] = scores
        outputMap[3] = length
        val logs = ArrayList<Pair<Double, Double>>()
        val results = ArrayList<TFResult>()
        if (interpreter != null && running) {
            interpreter.runForMultipleInputsOutputs(arrayOf<Any>(input), outputMap)
            var i = 0
            while (i < length[0] - 1) {
                //                Log.d("aaaa", String.valueOf(i));
                if (MachineConstants.gameConstants.labelThreshold()[labels[0][i].toInt()] != null) {
                    if (scores[0][i] >= MachineConstants.gameConstants.labelThreshold()[labels[0][i].toInt()]!!) {
                        Log.d("LABEL_TFP", "" + labels[0][i]) //threshold
                        val l = labels[0][i]
                        val box = boxes[0][i]
                        for (j in boxes[0][i].indices) {
                            if (boxes[0][i][j] < 0f) {
                                boxes[0][i][j] = 0f
                            }
                        }
                        results.add(TFResult(l.toInt(), scores[0][i], box, resolution!!, ""))
                    } else {
                        if (loggingAgent != null) {
                            logs.add(
                                Pair.create(
                                    Math.round(
                                        labels[0][i] * 1000.0
                                    ) / 1000.0, Math.round(scores[0][i] * 1000.0) / 1000.0
                                )
                            )
                        }
                    }
                }
                i++
            }
        }
        logOutput(logs, "x")
        return results
    }

    private fun logOutput(logs: ArrayList<Pair<Double, Double>>, fileName: String) {
        try {
            val map: MutableMap<Double, Double> = HashMap()
            logs.forEach(Consumer { it: Pair<Double, Double> ->
                map[it.first] = if (map.containsKey(it.first)) Math.max(
                    map[it.first]!!, it.second
                ) else it.second
            })

            val message = StringBuilder(fileName + "Low confidence: ")
            map.forEach { (k: Double?, v: Double?) ->
                message.append("[ ").append(k).append(" = ").append(v).append(" ], ")
            }
            Log.i("LOGGER", "Labels $message")
            log{ builder: GameLogMessage.Builder ->
                builder.setMessage("low_confidence")
                builder.addContext("low_confidence", message.toString())
                builder.addContext("confidence", map)
            }

        } catch (e: Exception) {
            log("Labels " + e.message)
            Log.i("LOGGER", "Labels " + e.message)
        }
    }
}

