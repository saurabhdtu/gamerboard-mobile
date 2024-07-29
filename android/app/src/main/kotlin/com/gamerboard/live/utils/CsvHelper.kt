package com.gamerboard.live.utils

import java.lang.Exception

class CsvHelper() {

    private val fields = mutableMapOf<String, MutableList<String?>>()

    fun addField(key: String, value: String?) {
        if (fields.containsKey(key)) {
            fields[key]?.add(value)
        } else {
            fields[key] = mutableListOf(value)
        }
    }

    fun generateCSV(): String {
        val stringBuilder = StringBuilder()

        // Append header
        stringBuilder.append(fields.keys.joinToString(",")).append("\n")

        // Append values
        val maxRowCount = fields.values.maxOf { it.size }
        for (rowIndex in 0 until maxRowCount) {
            fields.keys.forEachIndexed { index, key ->
                val rowValues = fields[key]
                val value = rowValues?.getOrNull(rowIndex) ?: ""
                stringBuilder.append(value.replace(",", ";"))
                if (index < fields.size - 1) {
                    stringBuilder.append(",")
                } else {
                    stringBuilder.append("\n")
                }
            }
        }

        return stringBuilder.toString()
    }

    fun parseCSV(csvString: String) {
        val lines = csvString.lines()
        if (lines.isNotEmpty()) {
            val headers = lines[0].split(",")
            for (i in 1 until lines.size) {
                val values = lines[i].split(",")
                for (j in headers.indices) {
                    val header = headers[j]
                    val value = if (j < values.size) values[j] else ""
                    addField(header, value)
                }
            }
        }
    }
}