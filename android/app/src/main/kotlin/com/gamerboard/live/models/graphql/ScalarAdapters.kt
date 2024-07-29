package com.gamerboard.live.models.graphql

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import com.gamerboard.live.utils.logException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by saurabh.lahoti on 02/05/22
 */

object DateAdapter : Adapter<Date> {
    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): Date {
        var date = reader.nextString();
        return try {
            try{
                val dateTimeFormatter =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateTimeFormatter.parse(date)
            }catch (e:Exception) {
                val dateTimeFormatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                dateTimeFormatter.parse(date)
            }
        }catch(ex:Exception) {
            logException(ex)
            Date();
        }
    }

    override fun toJson(
        writer: JsonWriter,
        customScalarAdapters: CustomScalarAdapters,
        value: Date
    ) {
        val dateTimeFormatter =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss").apply { timeZone = TimeZone.getTimeZone("UTC") }
        writer.value(dateTimeFormatter.format(value))
    }
}
